@echo off
echo Compiling...
javac -encoding UTF-8 -cp ".;json-20230227.jar;mssql-jdbc-9.2.1.jre11.jar" PregnancyAssistant.java DatabaseManager.java
if %errorlevel% neq 0 exit /b %errorlevel%

echo Running on Port 8081...
java -Dfile.encoding=UTF-8 -cp ".;json-20230227.jar;mssql-jdbc-9.2.1.jre11.jar" PregnancyAssistant
pause
