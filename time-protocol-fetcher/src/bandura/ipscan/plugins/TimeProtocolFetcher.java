// SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.time.Instant;
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
    private static final @NonNull Logger LOG = LoggerFactory.getLogger();

    private static byte[] readTimeBytesFromInputStream(final @NonNull InputStream is) throws IOException {
        final byte[] time1 = is.readNBytes(4);

        if (time1.length < 4) {
            return time1;
        }

        final byte[] time2 = is.readNBytes(4);
        if (time2.length == 4) {
            /* eight bytes read */
            final byte[] result = new byte[8];
            System.arraycopy(time1, 0, result, 0, 4);
            System.arraycopy(time2, 0, result, 4, 4);
            return result;
        }

        /* only four bytes read */
        return time1;
    }

    @CheckReturnValue
    private static long rfc868BytesToRfc868Timestamp(final byte[] time) {
        if (time.length == 4) {
            return Integer.toUnsignedLong(ByteBuffer.wrap(time).getInt());
        } else {
            return ByteBuffer.wrap(time).getLong();
        }
    }

    @CheckReturnValue
    private static long rfc868TimestampToUnixTimestamp(final long rfc868timestamp) {
        return rfc868timestamp - 2_208_988_800L;
    }

    private final @NonNull ScannerConfig scannerConfig;

    public TimeProtocolFetcher(final @NonNull ScannerConfig scannerConfig) {
        super();
        this.scannerConfig = scannerConfig;
    }

    @Override
    @CheckReturnValue
    @NonNull
    public String getId() {
        return "fetcher.timeProtocolFetcher";
    }

    @Override
    @CheckReturnValue
    @Nullable
    public Object scan(final @NonNull ScanningSubject subject) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(subject.getAddress(), TIME_PORT), subject.getAdaptedPortTimeout());
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(scannerConfig.portTimeout * 2);
            socket.setSoLinger(true, 0);

            final byte[] timeResponse = readTimeBytesFromInputStream(socket.getInputStream());
            switch (timeResponse.length) {
                case 4, 8: {
                    subject.setResultType(ResultType.WITH_PORTS);

                    final long timestamp = rfc868TimestampToUnixTimestamp(rfc868BytesToRfc868Timestamp(timeResponse));

                    if (timestamp < Instant.MIN.getEpochSecond() || timestamp > Instant.MAX.getEpochSecond()) {
                        return null;
                    }

                    return Instant.ofEpochSecond(timestamp).toString();
                }

                default: {
                    return null;
                }
            }
        } catch (SocketTimeoutException | SocketException e) {
            // no open port
        } catch (IOException e) {
            LOG.log(Level.FINE, () -> String.format("%s: %s", subject.getAddress().toString(), e.getStackTrace()));
        }
        return null;
    }

}
