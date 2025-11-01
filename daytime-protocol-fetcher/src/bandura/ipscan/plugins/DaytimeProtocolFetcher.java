// SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.fetchers.PortTextFetcher;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Fetcher, which retrieves the daytime.
 */
public class DaytimeProtocolFetcher extends PortTextFetcher {
    private static final int DAYTIME_PORT = 13;
    private static final String REGEX = "^(.*)$";

    public DaytimeProtocolFetcher(@NonNull ScannerConfig scannerConfig) {
        super(scannerConfig, DAYTIME_PORT, "", REGEX);
    }

    @CheckReturnValue
    @NonNull
    @Override
    public String getId() {
        return "fetcher.daytimeProtocolFetcher";
    }

}
