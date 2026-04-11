@echo off

:: clean old files
echo Cleaning old files...
if exist bin rd /s /q bin
mkdir bin

:: compile java code
echo Compiling Java code...
:: include both src and lib folders
javac -d bin -cp "bin;lib/*" src/MainApp.java src/com/matridisha/model/*.java

:: check if compilation failed
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please check if your Java files are in the right folder.
    pause
    exit /b
)

:: run the server
echo Starting MatriDisha Server...
:: use bin folder while running
java -cp "bin;lib/*" MainApp

pause