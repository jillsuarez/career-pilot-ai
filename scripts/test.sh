#!/bin/sh
set -e

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT_DIR"

if ! command -v java >/dev/null 2>&1; then
    echo "Java is required. Install Java 21 or newer, then run this again."
    exit 1
fi

./mvnw -Dmaven.repo.local=.m2/repository test
