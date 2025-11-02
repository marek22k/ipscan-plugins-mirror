// SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.regex.qual.Regex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.AbstractFetcher;

/**
 * Fetcher, which retrieves the Mikrotik RouterOS version via the WinBox API
 */
public class MikrotikRouterOSVersionFetcher extends AbstractFetcher {
    private static final int WINBOX_PORT = 8291;
    private static final byte[] PAYLOAD = {(byte) 0x12, (byte) 0x02, (byte) 'l', (byte) 'i', (byte) 's', (byte) 't',
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    private static final @Regex(1) @NonNull Pattern REGEX = Pattern.compile(" version: \"([0-9.]+)\" ");
    private static final @NonNull Logger LOG = LoggerFactory.getLogger();

    private final @NonNull ScannerConfig scannerConfig;

    public MikrotikRouterOSVersionFetcher(final @NonNull ScannerConfig scannerConfig) {
        super();
        this.scannerConfig = scannerConfig;
    }

    @Override
    @CheckReturnValue
    @NonNull
    public String getId() {
        return "fetcher.mikrotikRouterOSVersionFetcher";
    }

    @SuppressWarnings("nullness:dereference.of.nullable")
    @Override
    @CheckReturnValue
    @Nullable
    public Object scan(final @NonNull ScanningSubject subject) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(subject.getAddress(), WINBOX_PORT), subject.getAdaptedPortTimeout());
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(scannerConfig.portTimeout * 2);
            socket.setSoLinger(true, 0);

            final OutputStream os = socket.getOutputStream();
            os.write(PAYLOAD);
            os.flush();

            final BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.ISO_8859_1)
            );
            String line;
            while (true) {
                line = in.readLine();
                if (line == null) {
                    break;
                }

                final Matcher matcher = REGEX.matcher(line);
                if (matcher.find()) {
                    // mark that additional info is available
                    subject.setResultType(ResultType.WITH_PORTS);
                    @SuppressWarnings("nullness:assignment") final String result = matcher.group(1);

                    if (result.isEmpty()) {
                        return String.valueOf(WINBOX_PORT);
                    } else {
                        return result;
                    }
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
