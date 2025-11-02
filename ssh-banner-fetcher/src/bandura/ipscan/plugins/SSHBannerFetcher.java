// SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.regex.qual.Regex;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.fetchers.PortTextFetcher;

/**
 * Fetcher, which retrieves the SSH banner
 */
public class SSHBannerFetcher extends PortTextFetcher {
    private static final int SSH_PORT = 22;
    private static final @Regex(1) @NonNull String REGEX = "^(.*)$";

    public SSHBannerFetcher(final ScannerConfig scannerConfig) {
        super(scannerConfig, SSH_PORT, "", REGEX);
    }

    @Override
    @CheckReturnValue
    @NonNull
    public String getId() {
        return "fetcher.sshBanner";
    }

}
