# Career Pilot AI Setup Guide

This guide explains how to run Career Pilot AI locally on Mac and Windows.

Use emulator mode first if the team does not have Firebase keys yet. Emulator mode does not need a Firebase service-account JSON file.

## What Runs Locally

You will run three things:

1. Firestore Emulator
2. Spring Boot backend
3. JavaFX frontend

Default local URLs:

```text
Backend: http://localhost:8080
Firestore Emulator UI: http://localhost:4000
Firestore Emulator host: localhost:8081
```

## Required Tools

Install these before running the app:

- Java 21 or newer
- Node.js and npm
- Firebase CLI
- Gemini API key if you want AI generation to work

Check Java:

Mac:

```bash
java -version
```

Windows PowerShell:

```powershell
java -version
```

If Java is missing, install Java 21 or newer:

[Adoptium Java downloads](https://adoptium.net/temurin/releases/)

Check npm:

Mac:

```bash
npm -v
```

Windows PowerShell:

```powershell
npm -v
```

If npm is missing, install Node.js:

[Node.js downloads](https://nodejs.org/en/download)

Install Firebase CLI:

Mac:

```bash
npm install -g firebase-tools
```

Windows PowerShell:

```powershell
npm install -g firebase-tools
```

Check Firebase CLI:

Mac:

```bash
firebase --version
```

Windows PowerShell:

```powershell
firebase --version
```

## Key Rules

- Do not commit API keys.
- Do not commit Firebase service-account JSON files.
- Do not put keys directly into Java files.
- Do not paste secrets into screenshots or Discord.
- Firebase Web API keys are not needed for this app.
- This app uses Firebase Admin/server credentials from the backend.
- Local emulator mode does not need Firebase keys.

## Local Setup With Firestore Emulator

Use this setup first. It is the easiest local path.

No Firebase service-account key is needed.

Default emulator values:

```text
FIREBASE_PROJECT_ID=demo-career-pilot-ai
USE_FIREBASE_EMULATOR=true
FIRESTORE_EMULATOR_HOST=localhost:8081
```

## `.env` File

The scripts automatically load `.env` if it exists.

Create it from the example file.

Mac:

```bash
cp .env.example .env
```

Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Emulator `.env` example:

```text
FIREBASE_PROJECT_ID=demo-career-pilot-ai
USE_FIREBASE_EMULATOR=true
FIRESTORE_EMULATOR_HOST=localhost:8081

GEMINI_API_KEY=your-gemini-api-key
GEMINI_MODEL=gemini-3.5-flash
```

`GEMINI_API_KEY` can be blank. The backend will still start, but resume and cover letter generation will show a clear error until the key is set.

Do not commit `.env`.

## Mac Quickstart With Emulator

Open Terminal 1:

```bash
chmod +x mvnw scripts/*.sh
cp .env.example .env
./scripts/run-firestore-emulator.sh
```

Leave Terminal 1 running.

Open Terminal 2:

```bash
./scripts/run-backend.sh
```

Leave Terminal 2 running.

Open Terminal 3:

```bash
./scripts/run-frontend.sh
```

The JavaFX app should open.

## Windows Quickstart With Emulator

Open PowerShell 1:

```powershell
Copy-Item .env.example .env
.\scripts\run-firestore-emulator.ps1
```

Leave PowerShell 1 running.

Open PowerShell 2:

```powershell
.\scripts\run-backend.ps1
```

Leave PowerShell 2 running.

Open PowerShell 3:

```powershell
.\scripts\run-frontend.ps1
```

The JavaFX app should open.

If PowerShell blocks scripts, run this in the project folder:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

Then run the script again.

## Gemini API Key Setup

Gemini is used for resume and cover letter generation.

Get a Gemini API key:

1. Go to [Google AI Studio API Keys](https://aistudio.google.com/app/apikey).
2. Sign in with a Google account.
3. Click **Create API key**.
4. Choose the Google Cloud project if asked.
5. Copy the key.
6. Put the key in `.env`.

Recommended `.env` values:

```text
GEMINI_API_KEY=your-gemini-api-key
GEMINI_MODEL=gemini-3.5-flash
```

Prefer a new Gemini key for this project instead of an old unrestricted key.

Set Gemini manually without `.env`:

Mac:

```bash
export GEMINI_API_KEY="your-gemini-api-key"
export GEMINI_MODEL="gemini-3.5-flash"
```

Windows PowerShell:

```powershell
$env:GEMINI_API_KEY="your-gemini-api-key"
$env:GEMINI_MODEL="gemini-3.5-flash"
```

## Real Firebase Setup

Use this only after someone creates the Firebase project and service-account key.

The backend needs a Firebase Admin service-account JSON file.

Get the Firebase service-account JSON:

1. Go to [Firebase Console](https://console.firebase.google.com/).
2. Open the project.
3. Click the gear icon.
4. Click **Project settings**.
5. Click **Service accounts**.
6. Click **Generate new private key**.
7. Save the downloaded JSON file as:

```text
secrets/firebase-service-account.json
```

Do not commit this JSON file.

Real Firebase `.env` example:

```text
FIREBASE_PROJECT_ID=your-firebase-project-id
GOOGLE_APPLICATION_CREDENTIALS=/absolute/path/to/secrets/firebase-service-account.json

GEMINI_API_KEY=your-gemini-api-key
GEMINI_MODEL=gemini-3.5-flash
```

Mac example:

```bash
export FIREBASE_PROJECT_ID="your-firebase-project-id"
export GOOGLE_APPLICATION_CREDENTIALS="$PWD/secrets/firebase-service-account.json"
export GEMINI_API_KEY="your-gemini-api-key"
export GEMINI_MODEL="gemini-3.5-flash"
unset USE_FIREBASE_EMULATOR
unset FIRESTORE_EMULATOR_HOST
./scripts/run-backend.sh
```

Windows PowerShell example:

```powershell
$env:FIREBASE_PROJECT_ID="your-firebase-project-id"
$env:GOOGLE_APPLICATION_CREDENTIALS="$PWD\secrets\firebase-service-account.json"
$env:GEMINI_API_KEY="your-gemini-api-key"
$env:GEMINI_MODEL="gemini-3.5-flash"
Remove-Item Env:\USE_FIREBASE_EMULATOR -ErrorAction SilentlyContinue
Remove-Item Env:\FIRESTORE_EMULATOR_HOST -ErrorAction SilentlyContinue
.\scripts\run-backend.ps1
```

## Manual Commands

Use these if you do not want to use scripts.

Start Firestore Emulator:

Mac:

```bash
firebase emulators:start --only firestore --project demo-career-pilot-ai
```

Windows PowerShell:

```powershell
firebase emulators:start --only firestore --project demo-career-pilot-ai
```

Start backend:

Mac:

```bash
FIREBASE_PROJECT_ID=demo-career-pilot-ai USE_FIREBASE_EMULATOR=true FIRESTORE_EMULATOR_HOST=localhost:8081 ./mvnw -Dmaven.repo.local=.m2/repository -pl backend spring-boot:run
```

Windows PowerShell:

```powershell
$env:FIREBASE_PROJECT_ID="demo-career-pilot-ai"
$env:USE_FIREBASE_EMULATOR="true"
$env:FIRESTORE_EMULATOR_HOST="localhost:8081"
.\mvnw.cmd -Dmaven.repo.local=.m2\repository -pl backend spring-boot:run
```

Start frontend:

Mac:

```bash
./mvnw -Dmaven.repo.local=.m2/repository -pl frontend javafx:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd -Dmaven.repo.local=.m2\repository -pl frontend javafx:run
```

Run tests:

Mac:

```bash
./scripts/test.sh
```

Windows PowerShell:

```powershell
.\scripts\test.ps1
```

Manual test command:

Mac:

```bash
./mvnw -Dmaven.repo.local=.m2/repository test
```

Windows PowerShell:

```powershell
.\mvnw.cmd -Dmaven.repo.local=.m2\repository test
```

## Main API Routes

- `GET /api/resume-profile`
- `POST /api/resume-profile`
- `POST /api/generate/resume`
- `POST /api/generate/cover-letter`
- `GET /api/documents`
- `GET /api/documents/{id}`
- `POST /api/documents`

## Firestore Data Layout

Until login is added, all data is stored under one demo user:

```text
users/demo-user/resumeProfiles/current
users/demo-user/generatedDocuments/{documentId}
```

## Smoke Test

After starting the emulator, backend, and frontend:

1. Open the JavaFX app.
2. Go to **Resume Profile**.
3. Enter a full name, education, and skills.
4. Save the profile.
5. Go to **Generate**.
6. Enter a company, job title, and job description.
7. Generate a resume or cover letter.
8. Edit the generated text if needed.
9. Save the output.
10. Go to **Saved Documents**.
11. Confirm the saved document appears.

If `GEMINI_API_KEY` is missing, steps 7-9 will show a Gemini key error. That is expected until the key is added.

## Troubleshooting

### `java` command not found

Install Java 21 or newer:

[Adoptium Java downloads](https://adoptium.net/temurin/releases/)

Then reopen the terminal.

### `npm` command not found

Install Node.js:

[Node.js downloads](https://nodejs.org/en/download)

Then reopen the terminal.

### `firebase` command not found

Install Firebase CLI:

```bash
npm install -g firebase-tools
```

Then reopen the terminal.

### PowerShell blocks scripts

Run this in the project folder:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

Then run the script again.

### Backend says `FIREBASE_PROJECT_ID is required`

Set it in `.env`:

```text
FIREBASE_PROJECT_ID=demo-career-pilot-ai
```

Or set it manually:

Mac:

```bash
export FIREBASE_PROJECT_ID="demo-career-pilot-ai"
```

Windows PowerShell:

```powershell
$env:FIREBASE_PROJECT_ID="demo-career-pilot-ai"
```

### Backend says `FIRESTORE_EMULATOR_HOST is required`

Set emulator values in `.env`:

```text
USE_FIREBASE_EMULATOR=true
FIRESTORE_EMULATOR_HOST=localhost:8081
```

Then start the emulator:

Mac:

```bash
./scripts/run-firestore-emulator.sh
```

Windows PowerShell:

```powershell
.\scripts\run-firestore-emulator.ps1
```

### Backend says `GOOGLE_APPLICATION_CREDENTIALS must point to your Firebase service-account JSON file`

Use emulator mode:

```text
USE_FIREBASE_EMULATOR=true
FIRESTORE_EMULATOR_HOST=localhost:8081
```

Or set a real Firebase service-account path:

Mac:

```bash
export GOOGLE_APPLICATION_CREDENTIALS="$PWD/secrets/firebase-service-account.json"
```

Windows PowerShell:

```powershell
$env:GOOGLE_APPLICATION_CREDENTIALS="$PWD\secrets\firebase-service-account.json"
```

### AI generation says Gemini key is missing

Set `GEMINI_API_KEY` in `.env`:

```text
GEMINI_API_KEY=your-gemini-api-key
```

Then restart the backend.

### Frontend cannot connect

Start the backend first.

Mac:

```bash
./scripts/run-backend.sh
```

Windows PowerShell:

```powershell
.\scripts\run-backend.ps1
```

### First Maven run is slow

That is normal. Maven downloads dependencies into `.m2/repository`.

## Reference Links

- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Admin SDK setup](https://firebase.google.com/docs/admin/setup)
- [Firestore documentation](https://firebase.google.com/docs/firestore)
- [Google AI Studio API Keys](https://aistudio.google.com/app/apikey)
- [Gemini API documentation](https://ai.google.dev/gemini-api/docs)
