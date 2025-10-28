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
	./gradlew dependencyUpdates

docs:
	./gradlew javadoc

clean:
	./gradlew clean
