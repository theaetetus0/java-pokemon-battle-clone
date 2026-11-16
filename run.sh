#!/usr/bin/env bash
# Compile and run the game without needing Maven.
set -euo pipefail
cd "$(dirname "$0")"
rm -rf build/classes
javac --release 17 -d build/classes src/main/java/*.java
java -cp build/classes Main
