@echo off
echo Compiling Database Manager...
javac -encoding UTF-8 -cp ".;json-20230227.jar;mssql-jdbc-9.2.1.jre11.jar" DatabaseManager.java
if %errorlevel% neq 0 exit /b %errorlevel%

echo Migrating history to SQL Server...
java -Dfile.encoding=UTF-8 -cp ".;json-20230227.jar;mssql-jdbc-9.2.1.jre11.jar" DatabaseManager
pause
