$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Parent $PSScriptRoot
Set-Location $RootDir

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "Java is required. Install Java 21 or newer, then run this again."
    exit 1
}

.\mvnw.cmd -Dmaven.repo.local=.m2\repository test
