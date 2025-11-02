// SPDX-FileCopyrightText: Copyright (C) 2025 Anton Keks
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Date;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.exporters.TXTExporter;

/**
 * Leetspeak exporter, inspired by nmap's -oS
 */
public class LeetspeakExporter extends TXTExporter {
    @Override
    @CheckReturnValue
    @NonNull
    public String getFilenameExtension() {
        return "stxt";
    }

    @Override
    @CheckReturnValue
    @NonNull
    public String getId() {
        return "exporter.leatspeak";
    }

    @Override
    public void start(OutputStream outputStream, String feederInfo) throws IOException {
        output = new PrintWriter(
                new LeetspeakFilterWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
        );
        // super.start(outputStream, feederInfo);

        if (!append) {
            output.print(Labels.getLabel("exporter.txt.generated"));
            output.print(' ');
            output.println(Version.getFullName());
            output.println(Version.WEBSITE);
            output.println();

            output.print(Labels.getLabel("exporter.txt.scanned"));
            output.print(' ');
            output.println(feederInfo);
            output.println(DateFormat.getDateTimeInstance().format(new Date()));
            output.println();
        }
    }
}
