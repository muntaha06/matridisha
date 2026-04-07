@echo off
echo Cleaning old files...
if exist bin rd /s /q bin
mkdir bin

echo Compiling Java code...
:: এখানে src এবং lib উভয়কেই গুরুত্ব দেওয়া হয়েছে
javac -d bin -cp "bin;lib/*" src/MainApp.java src/com/matridisha/model/*.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please check if your Java files are in the right folder.
    pause
    exit /b
)

echo Starting MatriDisha Server...
:: রান করার সময়ও bin ফোল্ডারটি ধরিয়ে দেওয়া হয়েছে
java -cp "bin;lib/*" MainApp
pause