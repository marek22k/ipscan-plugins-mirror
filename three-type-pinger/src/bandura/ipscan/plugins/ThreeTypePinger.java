// SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.IOException;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.AbstractPinger;
import net.azib.ipscan.core.net.JavaPinger;
import net.azib.ipscan.core.net.PingResult;
import net.azib.ipscan.core.net.TCPPinger;
import net.azib.ipscan.core.net.UDPPinger;

/**
 * Pinger, which combines Java build-in pinger, UDP Pinger and TCP Pinger
 */
public class ThreeTypePinger extends AbstractPinger {
    private JavaPinger javaPinger;
    private UDPPinger udpPinger;
    private TCPPinger tcpPinger;

    public ThreeTypePinger(@NonNull JavaPinger javaPinger, @NonNull TCPPinger tcpPinger, @NonNull UDPPinger udpPinger) {
        super();
        this.javaPinger = javaPinger;
        this.udpPinger = udpPinger;
        this.tcpPinger = tcpPinger;
    }

    @CheckReturnValue
    @NonNull
    @Override
    public String getId() {
        return "pinger.threeTypePinger";
    }

    @CheckReturnValue
    @NonNull
    @Override
    public PingResult ping(@NonNull ScanningSubject subject, int count) throws IOException {
        // try Java Build-in first - as it could use ICMP
        // minimum three tries to prevent packet loss in unreliable networks
        int javaBuiltinInitialCount = Math.max(3, count / 3);
        PingResult javaBuiltinResult = this.javaPinger.ping(subject, javaBuiltinInitialCount);
        if (javaBuiltinResult.isAlive()) {
            return javaBuiltinResult.merge(javaPinger.ping(subject, count - javaBuiltinInitialCount));
        }

        // try UDP second - it should be more reliable than TCP, but less than ICMP
        // minimum three tries to prevent packet loss in unreliable networks
        int udpCountInitialCount = Math.max(3, count / 3);
        PingResult udpResult = udpPinger.ping(subject, udpCountInitialCount);
        if (udpResult.isAlive()) {
            return udpResult.merge(udpPinger.ping(subject, count - udpCountInitialCount));
        }

        // fallback to TCP - it may detect some hosts Java Built-in, UDP cannot
        return tcpPinger.ping(subject, count);
    }
}
