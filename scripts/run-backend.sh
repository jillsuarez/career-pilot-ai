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

if [ -z "$FIREBASE_PROJECT_ID" ]; then
    export FIREBASE_PROJECT_ID="demo-career-pilot-ai"
fi

if [ "$USE_FIREBASE_EMULATOR" = "true" ]; then
    if [ -z "$FIRESTORE_EMULATOR_HOST" ]; then
        export FIRESTORE_EMULATOR_HOST="localhost:8081"
    fi
    echo "Using Firestore Emulator at $FIRESTORE_EMULATOR_HOST"
    echo "If it is not running, start it in another terminal:"
    echo "./scripts/run-firestore-emulator.sh"
elif [ -n "$GOOGLE_APPLICATION_CREDENTIALS" ]; then
    if [ ! -f "$GOOGLE_APPLICATION_CREDENTIALS" ]; then
        echo "Firebase service-account file was not found:"
        echo "$GOOGLE_APPLICATION_CREDENTIALS"
        exit 1
    fi
    echo "Using real Firebase project: $FIREBASE_PROJECT_ID"
else
    export USE_FIREBASE_EMULATOR="true"
    export FIRESTORE_EMULATOR_HOST="localhost:8081"
    echo "No Firebase service-account file was set."
    echo "Using Firestore Emulator by default."
    echo "Start it in another terminal:"
    echo "./scripts/run-firestore-emulator.sh"
fi

if [ -z "$GEMINI_API_KEY" ]; then
    echo "Warning: GEMINI_API_KEY is not set. The backend will start, but AI generation will show an error."
fi

./mvnw -Dmaven.repo.local=.m2/repository -pl backend spring-boot:run
