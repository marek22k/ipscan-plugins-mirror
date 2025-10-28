# SPDX-FileCopyrightText: Copyright (C) 2025 Marek Küthe <m.k@mk16.de>
#
# SPDX-License-Identifier: GPL-3.0-or-later

MAKEFLAGS := -j1

.PHONY: all fatJar proguardedJar check pmd spotbugs checkdependencies docs clean format lizard reuse-annotate reuse-download reuse-lint reuse-fix reuse

all: check proguardedJar

fatJar:
	./gradlew fatJar

proguardedJar:
	./gradlew proguardedJar

check:
	./gradlew check

pmd:
	./gradlew pmdMain

spotbugs:
	./gradlew spotbugsMain

checkdependencies:
	./gradlew dependencyUpdates --refresh-dependencies

docs:
	./gradlew javadoc

clean:
	./gradlew clean

format:
	./gradlew spotlessApply

lizard:
	./gradlew lizard

reuse-annotate:
	./gradlew reuseAnnotate

reuse-download:
	./gradlew reuseDownload

reuse-lint:
	./gradlew reuseLint

reuse-fix: reuse-annotate reuse-download

reuse: reuse-lint
