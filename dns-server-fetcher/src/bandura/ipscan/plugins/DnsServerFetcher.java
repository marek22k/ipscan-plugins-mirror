// SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.AbstractFetcher;

/**
 * Fetcher that checks if a and which DNS server is running.
 */
public class DnsServerFetcher extends AbstractFetcher {
    private static final int DNS_PORT = 53;
    private static final String[] versions_records = {"version.bind.", "version.server.", "authors.bind.",
            "hostname.bind.", "id.server."};
    private static final @NonNull Logger LOG = LoggerFactory.getLogger();

    private @NonNull ScannerConfig scannerConfig;

    public DnsServerFetcher(@NonNull ScannerConfig scannerConfig) {
        super();
        this.scannerConfig = scannerConfig;
    }

    @Override
    @CheckReturnValue
    @NonNull
    public String getId() {
        return "fetcher.dnsServerFetcher";
    }

    @Override
    @CheckReturnValue
    @Nullable
    public Object scan(@NonNull ScanningSubject subject) {
        Resolver r = new SimpleResolver(subject.getAddress());
        r.setPort(DNS_PORT);
        r.setTCP(true);
        r.setTimeout(Duration.ofSeconds(scannerConfig.portTimeout * 2));

        for (String version_record : versions_records) {
            try {
                Lookup l = new Lookup(version_record, Type.TXT, DClass.CH);
                l.setResolver(r);
                l.run();
                int status = l.getResult();
                if (status == Lookup.HOST_NOT_FOUND || status == Lookup.SUCCESSFUL || status == Lookup.TYPE_NOT_FOUND) {
                    subject.setResultType(ResultType.WITH_PORTS);
                }
                if (status == Lookup.SUCCESSFUL) {
                    Record[] answers = l.getAnswers();
                    if (answers.length > 0) {
                        String server = answers[0].rdataToString();
                        if (server.length() >= 2 && server.startsWith("\"") && server.endsWith("\"")) {
                            return server.substring(1, server.length() - 1);
                        }
                        return server;
                    }
                }
            } catch (TextParseException e) {
                LOG.log(Level.WARNING, version_record, e);
            }
        }

        return null;
    }

}
