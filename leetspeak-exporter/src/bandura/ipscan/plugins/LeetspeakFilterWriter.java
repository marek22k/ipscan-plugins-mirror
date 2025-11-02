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

    public LeetspeakFilterWriter(@NonNull Writer out) {
        super(out);
        leetspeakSwitch = true;
    }

    @Override
    public void write(@NonNull String str, int off, int len) throws IOException {
        write(str.toCharArray(), off, len);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        char[] characters = new char[len];
        for (int index = 0; index < len; index++) {
            characters[index] = toLeetspeak(cbuf[off + index]);
        }
        super.write(characters, 0, len);
    }

    @Override
    public void write(int c) throws IOException {
        super.write(toLeetspeak((char) c));
    }

    @CheckReturnValue
    private char toLeetspeak(char c) {
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
