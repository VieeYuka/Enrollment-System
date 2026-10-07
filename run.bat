@echo off
cd /d "%~dp0"
set CP=lib\jbcrypt-0.4.jar;lib\mysql-connector-j-9.4.0.jar
if not exist out mkdir out
javac -encoding UTF-8 -cp "%CP%" -d out src\oopSource\*.java || exit /b 1
copy /y img\*.png out\ >nul
java -cp "out;%CP%" oopSource.FinalFrameOOP
