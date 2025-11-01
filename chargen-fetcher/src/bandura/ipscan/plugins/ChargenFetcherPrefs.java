// SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

import java.util.prefs.Preferences;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherPrefs;
import net.azib.ipscan.gui.AbstractModalDialog;
import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ChargenFetcherPrefs extends AbstractModalDialog implements FetcherPrefs {
    private @Nullable ChargenFetcher fetcher;

    public ChargenFetcherPrefs() {
        super();
        fetcher = null;
    }

    @Override
    public void openFor(Fetcher fetcher) {
        if (!(fetcher instanceof ChargenFetcher)) {
            throw new IllegalArgumentException(String.format("Invalid fetcher: %s", fetcher.getClass().getName()));
        }
        this.fetcher = (ChargenFetcher) fetcher;
        open();
    }

    @SuppressWarnings("nullness:dereference.of.nullable")
    @Override
    @RequiresNonNull({"fetcher"})
    protected void populateShell() {
        shell = new Shell(Display.getCurrent().getActiveShell(), SWT.DIALOG_TRIM);

        shell.setText(fetcher.getName());
        shell.setLayout(LayoutHelper.formLayout(10, 10, 5));

        Label linesLabel = new Label(shell, SWT.NONE);
        linesLabel.setText(Labels.getLabel("text.fetcher.chargenFetcher.linesToCheckAdditionally"));

        Text linesText = new Text(shell, SWT.BORDER);
        linesText.setText(String.valueOf(fetcher.getLinesToCheckAdditionally()));

        linesText.setLayoutData(
                LayoutHelper
                        .formData(new FormAttachment(0), new FormAttachment(100), new FormAttachment(linesLabel), null)
        );
        // linesLabel
        // .setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new
        // FormAttachment(linesText), null));

        Button okButton = new Button(shell, SWT.NONE);
        okButton.setText(Labels.getLabel("button.OK"));

        Button cancelButton = new Button(shell, SWT.NONE);
        cancelButton.setText(Labels.getLabel("button.cancel"));

        positionButtonsInFormLayout(okButton, cancelButton, linesText);

        okButton.addListener(SWT.Selection, e -> {
            Preferences prefs = fetcher.getPreferences();

            int lines = Integer.parseInt(linesText.getText());
            if (lines < 0) {
                throw new IllegalArgumentException(
                        String.format("%s: %d", Labels.getLabel("exception.fetcher.chargenFetcher.noIsNegative"), lines)
                );
            }

            fetcher.setLinesToCheckAdditionally(lines);
            prefs.putInt("linesToCheckAdditionally", lines);

            close();
        });
        cancelButton.addListener(SWT.Selection, e -> close());

        shell.pack();
    }
}
