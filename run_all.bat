@echo off
title MatriDisha Master Startup
echo ==============================================
echo       Starting MatriDisha Full System...
echo ==============================================
echo.

echo [0] Stopping any previous Java processes...
taskkill /F /IM java.exe >nul 2>&1
taskkill /F /IM javaw.exe >nul 2>&1
timeout /t 2 /nobreak >nul

echo [1] Compiling and Starting Main Portal (Port 8000)...
cd "matri_disha_main"
javac SimpleHttpServer.java
start /B javaw SimpleHttpServer
cd ..

echo [2] Compiling and Starting Before Delivery (Port 8080)...
cd "before_delivery"
javac SimpleHttpServer.java
start /B javaw SimpleHttpServer
cd ..

echo [3] Compiling and Starting Chatbot (Port 8081)...
cd "chatbot"
javac -encoding UTF-8 -cp ".;json-20230227.jar" PregnancyAssistant.java
start /B javaw -cp ".;json-20230227.jar" PregnancyAssistant
cd ..

echo [4] Compiling and Starting After Delivery (Port 8082)...
cd "after_delivery"
javac SimpleHttpServer.java
start /B javaw SimpleHttpServer
cd ..

echo [5] Compiling and Starting Common Disease (Port 8083)...
cd "common_disease"
javac -d bin src/MainApp.java src/com/matridisha/model/*.java
start /B javaw -cp bin MainApp
cd ..

echo.
echo ==============================================
echo All servers are now silently running in the background!
echo No extra command prompt windows will pop up.
echo.
echo Launching your browser to MatriDisha Portal...
echo ==============================================
timeout /t 2 /nobreak >nul
start http://localhost:8000
pause
