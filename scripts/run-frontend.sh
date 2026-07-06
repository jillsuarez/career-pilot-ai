#!/bin/sh
set -e

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT_DIR"

if ! command -v java >/dev/null 2>&1; then
    echo "Java is required. Install Java 21 or newer, then run this again."
    exit 1
fi

echo "Starting JavaFX frontend."
echo "Make sure the backend is running at http://localhost:8080"

./mvnw -Dmaven.repo.local=.m2/repository -pl frontend javafx:run
