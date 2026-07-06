#!/bin/sh
set -e

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT_DIR"

if [ -f ".env" ]; then
    set -a
    . "./.env"
    set +a
fi

if ! command -v java >/dev/null 2>&1; then
    echo "Java is required. Install Java 21 or newer, then run this again."
    exit 1
fi

if ! command -v firebase >/dev/null 2>&1; then
    echo "Firebase CLI is required."
    echo "Mac install: npm install -g firebase-tools"
    echo "Windows install: npm install -g firebase-tools"
    exit 1
fi

PROJECT_ID="${FIREBASE_PROJECT_ID:-demo-career-pilot-ai}"

echo "Starting Firestore Emulator for project: $PROJECT_ID"
echo "Firestore: localhost:8081"
echo "Emulator UI: http://localhost:4000"

firebase emulators:start --only firestore --project "$PROJECT_ID"
