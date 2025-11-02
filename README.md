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
| chargen-fetcher | Checks whether a chargen server is running. If so, checks whether it returns the pattern in the RFC. Returns `False` if no chargen server is running; returns `True (non-RFC pattern)` if no RFC patterns were returned; returns `True (partial RFC pattern, n/m)` if only the first n of m (configurable in the settings) lines matched the expected RFC pattern; and returns `True (RFC pattern)` if the received pattern completely matches the RFC pattern. Windows XP and xinetd's implementation do not fully implement the RFC pattern. |
| dns-server-fetcher | Attempt to determine the DNS server by querying the Chaos text record `version.bind.` and, if necessary, the additional `version.server.`, `authors.bind.`, `hostname.bind.` and `id.server.` records. |
| leetspeak-exporter | Leetspeak exporter, inspired by nmap's `-oS` |
| three-way-pinger | Pings uses the Java build-in, the UDP Pinger and the TCP Pinger |
