@echo off
setlocal

if "%MAVEN_VERSION%"=="" set MAVEN_VERSION=3.9.9
set BASE_DIR=%~dp0
set MAVEN_DIR=%BASE_DIR%.mvn\apache-maven-%MAVEN_VERSION%
set ARCHIVE=%BASE_DIR%.mvn\apache-maven-%MAVEN_VERSION%-bin.zip
set URL=https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip

if not exist "%MAVEN_DIR%\bin\mvn.cmd" (
    if not exist "%BASE_DIR%.mvn" mkdir "%BASE_DIR%.mvn"
    if not exist "%ARCHIVE%" (
        powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri '%URL%' -OutFile '%ARCHIVE%'"
    )
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '%ARCHIVE%' '%BASE_DIR%.mvn'"
)

call "%MAVEN_DIR%\bin\mvn.cmd" %*
