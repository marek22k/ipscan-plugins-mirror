<!--
SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>

SPDX-License-Identifier: GPL-3.0-or-later
-->

# ipscan-plugins

## Fetchers

| Plugin | Output |
| --- | --- |
| ssh-banner-fetcher | Retrieves the SSH banner and returns it. |
| mikrotik-routeros-version-fetcher | Retrieves the RouterOS version via the WinBox API and returns it. |
| echo-server-fetcher | Checks whether the echo server is working. Returns `True` if it is working and `False (n)` if it is not working, where n is the position (starting at 0) of the first byte that differs from the payload sent. 714 bytes are sent as payload. |
| time-protocol-fetcher | Retrieves the current time from a time server (RFC848) and returns it. |
| daytime-protocol-fetcher | Retrieves a time string via the Daytime protocol and returns it. |
