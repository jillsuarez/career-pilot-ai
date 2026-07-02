$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Parent $PSScriptRoot
Set-Location $RootDir

if (Test-Path ".env") {
    Get-Content ".env" | ForEach-Object {
        if ($_ -match "^\s*#" -or $_ -notmatch "=") { return }
        $parts = $_ -split "=", 2
        [Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), "Process")
    }
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "Java is required. Install Java 21 or newer, then run this again."
    exit 1
}

if ([string]::IsNullOrWhiteSpace($env:FIREBASE_PROJECT_ID)) {
    $env:FIREBASE_PROJECT_ID = "demo-career-pilot-ai"
}

if ($env:USE_FIREBASE_EMULATOR -eq "true") {
    if ([string]::IsNullOrWhiteSpace($env:FIRESTORE_EMULATOR_HOST)) {
        $env:FIRESTORE_EMULATOR_HOST = "localhost:8081"
    }
    Write-Host "Using Firestore Emulator at $env:FIRESTORE_EMULATOR_HOST"
    Write-Host "If it is not running, start it in another terminal:"
    Write-Host ".\scripts\run-firestore-emulator.ps1"
} elseif (-not [string]::IsNullOrWhiteSpace($env:GOOGLE_APPLICATION_CREDENTIALS)) {
    if (-not (Test-Path $env:GOOGLE_APPLICATION_CREDENTIALS)) {
        Write-Host "Firebase service-account file was not found:"
        Write-Host $env:GOOGLE_APPLICATION_CREDENTIALS
        exit 1
    }
    Write-Host "Using real Firebase project: $env:FIREBASE_PROJECT_ID"
} else {
    $env:USE_FIREBASE_EMULATOR = "true"
    $env:FIRESTORE_EMULATOR_HOST = "localhost:8081"
    Write-Host "No Firebase service-account file was set."
    Write-Host "Using Firestore Emulator by default."
    Write-Host "Start it in another terminal:"
    Write-Host ".\scripts\run-firestore-emulator.ps1"
}

if ([string]::IsNullOrWhiteSpace($env:GEMINI_API_KEY)) {
    Write-Host "Warning: GEMINI_API_KEY is not set. The backend will start, but AI generation will show an error."
}

.\mvnw.cmd -Dmaven.repo.local=.m2\repository -pl backend spring-boot:run
