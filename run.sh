#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

mkdir -p bin
echo "Compiling Senet..."
find src -name "*.java" > sources.txt
javac -d bin @sources.txt
rm -f sources.txt

echo "Launching Senet..."
java -cp bin senet.Senet
