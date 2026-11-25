#!/usr/bin/env bash
# Compile and run the game without needing Maven.
set -euo pipefail
cd "$(dirname "$0")"
rm -rf build/classes
mkdir -p build/classes
find src/main/java -name '*.java' -print0 | xargs -0 javac --release 17 -d build/classes
cp -r src/main/resources/. build/classes/
java -cp build/classes io.github.theaetetus0.pokemonbattle.Main
