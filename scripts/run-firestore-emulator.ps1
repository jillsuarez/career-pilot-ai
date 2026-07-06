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

if (-not (Get-Command firebase -ErrorAction SilentlyContinue)) {
    Write-Host "Firebase CLI is required."
    Write-Host "Install Node.js, then run: npm install -g firebase-tools"
    exit 1
}

$ProjectId = $env:FIREBASE_PROJECT_ID
if ([string]::IsNullOrWhiteSpace($ProjectId)) {
    $ProjectId = "demo-career-pilot-ai"
}

Write-Host "Starting Firestore Emulator for project: $ProjectId"
Write-Host "Firestore: localhost:8081"
Write-Host "Emulator UI: http://localhost:4000"

firebase emulators:start --only firestore --project $ProjectId
