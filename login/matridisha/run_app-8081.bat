@echo off
echo MatriDisha Server Starting...
javac -cp "lib/*" src/MainApp.java src/DatabaseManager.java src/PregnancyAssistant.java
java -cp "src;lib/*" MainApp
pause