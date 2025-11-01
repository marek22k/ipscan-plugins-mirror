// SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.AbstractFetcher;
import net.azib.ipscan.fetchers.FetcherPrefs;

/**
 * Fetcher that checks whether the a Chargen is running and working.
 */
public class ChargenFetcher extends AbstractFetcher {
    private static final int CHARGEN_PORT = 19;
    private static final Logger LOG = LoggerFactory.getLogger();
    private static final String RFC_PATTERN_CHARACTERS = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ ";

    @CheckReturnValue
    @NonNull
    private static String getRFCPattern(int no) {
        if (no < 0) {
            throw new IllegalArgumentException("Number is negative: " + no);
        }
        int start = no % (RFC_PATTERN_CHARACTERS.length() + 1);
        int end = Math.min(start + 72, RFC_PATTERN_CHARACTERS.length());
        int missingCharacters = 72 - (end - start);

        String result = RFC_PATTERN_CHARACTERS.substring(start, end);

        if (missingCharacters != 0) {
            result = result + RFC_PATTERN_CHARACTERS.substring(0, missingCharacters);
        }

        return result;
    }

    private ScannerConfig scannerConfig;
    private int linesToCheckAdditionally;

    public ChargenFetcher(ScannerConfig scannerConfig) {
        this.scannerConfig = scannerConfig;
        this.linesToCheckAdditionally = 2;
    }

    @CheckReturnValue
    @NonNull
    @Override
    public String getId() {
        return "fetcher.chargenFetcher";
    }

    @CheckReturnValue
    @NonNull
    public int getLinesToCheckAdditionally() {
        return this.linesToCheckAdditionally;
    }

    @CheckReturnValue
    @NonNull
    @Override
    public Class<? extends FetcherPrefs> getPreferencesClass() {
        return ChargenFetcherPrefs.class;
    }

    @CheckReturnValue
    @Nullable
    @Override
    public Object scan(ScanningSubject subject) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(subject.getAddress(), CHARGEN_PORT), subject.getAdaptedPortTimeout());
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(scannerConfig.portTimeout * 2);
            socket.setSoLinger(true, 0);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.ISO_8859_1)
            );
            String line = in.readLine();
            if (line == null || line.isBlank()) {
                return "False";
            } else {
                subject.setResultType(ResultType.WITH_PORTS);

                if (line.equals(getRFCPattern(0))) {
                    for (int no = 0; no < linesToCheckAdditionally; no++) {
                        int numberOfLines = no + 1; // including 0
                        String exprectedResponse = getRFCPattern(numberOfLines);
                        String response = in.readLine();
                        if (response == null || !response.equals(exprectedResponse)) {
                            return "True (partial RFC pattern, " + (numberOfLines + 1) + "/"
                                    + (linesToCheckAdditionally + 1) + ")";
                        }
                    }
                    return "True (RFC pattern)";
                } else {
                    return "True (non-RFC pattern)";
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

    public void setLinesToCheckAdditionally(int linesToCheckAdditionally) {
        if (linesToCheckAdditionally < 0) {
            throw new IllegalArgumentException("Value is negative: " + linesToCheckAdditionally);
        }
        this.linesToCheckAdditionally = linesToCheckAdditionally;
    }

}
