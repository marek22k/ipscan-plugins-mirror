// SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.fetchers.PortTextFetcher;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Fetcher, which retrieves the SSH banner
 */
public class SSHBannerFetcher extends PortTextFetcher {

    public SSHBannerFetcher(ScannerConfig scannerConfig) {
        super(scannerConfig, 22, "", "^(.*)$");
    }

    @CheckReturnValue
    @NonNull
    public String getId() {
        return "fetcher.sshBanner";
    }

}
