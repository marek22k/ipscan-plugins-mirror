// SPDX-FileCopyrightText: Copyright (C) 2025 Anton Keks
//
// SPDX-License-Identifier: GPL-3.0-or-later

package bandura.ipscan.plugins;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

class LeetspeakFilterWriter extends FilterWriter {
    private boolean leetspeakSwitch;

    public LeetspeakFilterWriter(final @NonNull Writer out) {
        super(out);
        leetspeakSwitch = true;
    }

    @Override
    public void write(final @NonNull String str, final int off, final int len) throws IOException {
        write(str.toCharArray(), off, len);
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        final char[] characters = new char[len];
        for (int index = 0; index < len; index++) {
            characters[index] = toLeetspeak(cbuf[off + index]);
        }
        super.write(characters, 0, len);
    }

    @Override
    public void write(final int c) throws IOException {
        super.write(toLeetspeak((char) c));
    }

    @CheckReturnValue
    private char toLeetspeak(final char c) {
        leetspeakSwitch = !leetspeakSwitch;

        if (!leetspeakSwitch) {
            return c;
        }

        switch (c) {
            case 'A': {
                return '4';
            }

            case 'B': {
                return '8';
            }

            case 'C': {
                return '(';
            }

            case 'e', 'E': {
                return '3';
            }

            case 'G': {
                return '6';
            }

            case 'i', 'I': {
                return '1';
            }

            case 'o', 'O': {
                return '0';
            }

            case 's', 'S': {
                return '$';
            }

            case 'z': {
                return 's';
            }

            case 'Z': {
                return 'S';
            }

            default: {
                return c;
            }
        }
    }
}
