@echo off
echo MatriDisha Project Compiling...

:: 1. Bin folder toiri kora (jodi na thake)
if not exist bin mkdir bin

:: 2. Java code compile kora
javac -d bin -encoding UTF-8 src/MainApp.java src/com/matridisha/model/*.java

:: 3. Compile thikmoto hole server run kora
if %errorlevel% equ 0 (
    echo Compilation Successful!
    echo Starting Server at http://localhost:8083...
    java -cp bin MainApp
) else (
    echo.
    echo [ERROR] Compilation failed. Please check your Java code!
    pause
)