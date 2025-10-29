// SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.HexFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.AbstractFetcher;

/**
 * Fetcher, which retrieves the current time from the target via the time
 * protocol.
 */
public class TimeProtocolFetcher extends AbstractFetcher {
    private static final int TIME_PORT = 37;
    private static final Logger LOG = LoggerFactory.getLogger();

    @CheckReturnValue
    @NonNull
    private static byte[] readTimeBytesFromInputStream(InputStream is) throws IOException {
        byte[] time1 = is.readNBytes(4);

        if (time1.length < 4) {
            return time1;
        }

        byte[] time2 = is.readNBytes(4);
        if (time2.length == 4) {
            /* eight bytes read */
            byte[] result = new byte[8];
            System.arraycopy(time1, 0, result, 0, 4);
            System.arraycopy(time2, 0, result, 4, 4);
            return result;
        }

        /* only four bytes read */
        return time1;
    }

    private ScannerConfig scannerConfig;

    public TimeProtocolFetcher(ScannerConfig scannerConfig) {
        this.scannerConfig = scannerConfig;
    }

    @CheckReturnValue
    @NonNull
    @Override
    public String getId() {
        return "fetcher.timeProtocolFetcher";
    }

    @CheckReturnValue
    @Nullable
    @Override
    public Object scan(ScanningSubject subject) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(subject.getAddress(), TIME_PORT), subject.getAdaptedPortTimeout());
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(scannerConfig.portTimeout * 2);
            socket.setSoLinger(true, 0);

            byte[] timeResponse = readTimeBytesFromInputStream(socket.getInputStream());
            System.out.println("Raw bytes (hex): " + HexFormat.of().formatHex(timeResponse));
            switch (timeResponse.length) {
                case 4, 8: {
                    subject.setResultType(ResultType.WITH_PORTS);

                    long timestamp = (timeResponse.length == 4
                            ? Integer.toUnsignedLong(ByteBuffer.wrap(timeResponse).getInt())
                            : ByteBuffer.wrap(timeResponse).getLong()) - 2208988800L;

                    if (timestamp < Instant.MIN.getEpochSecond() || timestamp > Instant.MAX.getEpochSecond()) {
                        return "TIME_INVALID: timestamp out of range (" + timestamp + ")";
                    }

                    Instant time = Instant.ofEpochSecond(timestamp);

                    return time.toString();
                }

                default: {
                    return null;
                }
            }
        } catch (ConnectException e) {
            // no connection
        } catch (SocketTimeoutException e) {
            // no information
        } catch (SocketException e) {
            // connection reset
        } catch (IOException e) {
            LOG.log(Level.FINE, subject.getAddress().toString(), e);
        }
        return null;
    }

}
