# SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
#
# SPDX-License-Identifier: GPL-3.0-or-later

MAKEFLAGS := -j1

.PHONY: all fatJar proguardedJar check checkdependencies docs clean

all: check proguardedJar

fatJar:
	./gradlew fatJar

proguardedJar:
	./gradlew proguardedJar

check:
	./gradlew check

checkdependencies:
	./gradlew dependencyUpdates --refresh-dependencies

docs:
	./gradlew javadoc

clean:
	./gradlew clean
