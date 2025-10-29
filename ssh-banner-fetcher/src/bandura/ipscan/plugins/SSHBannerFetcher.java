// SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.fetchers.PortTextFetcher;

/**
 * Fetcher, which retrieves the SSH banner
 */
public class SSHBannerFetcher extends PortTextFetcher {
    private static final int SSH_PORT = 22;
    private static final String REGEX = "^(.*)$";

    public SSHBannerFetcher(ScannerConfig scannerConfig) {
        super(scannerConfig, SSH_PORT, "", REGEX);
    }

    @CheckReturnValue
    @NonNull
    @Override
    public String getId() {
        return "fetcher.sshBanner";
    }

}
