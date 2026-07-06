#!/bin/sh
set -e

MAVEN_VERSION="${MAVEN_VERSION:-3.9.9}"
BASE_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MAVEN_DIR="$BASE_DIR/.mvn/apache-maven-$MAVEN_VERSION"
ARCHIVE="$BASE_DIR/.mvn/apache-maven-$MAVEN_VERSION-bin.tar.gz"
URL="https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz"

if [ ! -x "$MAVEN_DIR/bin/mvn" ]; then
    mkdir -p "$BASE_DIR/.mvn"
    if [ ! -f "$ARCHIVE" ]; then
        if command -v curl >/dev/null 2>&1; then
            curl -fsSL "$URL" -o "$ARCHIVE"
        elif command -v wget >/dev/null 2>&1; then
            wget -q "$URL" -O "$ARCHIVE"
        else
            echo "curl or wget is required to download Maven."
            exit 1
        fi
    fi
    tar -xzf "$ARCHIVE" -C "$BASE_DIR/.mvn"
fi

exec "$MAVEN_DIR/bin/mvn" "$@"
