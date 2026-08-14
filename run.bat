@echo off
setlocal

cd /d "%~dp0"

if not exist bin mkdir bin

echo Compiling Senet...
dir /s /b src\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt

echo Launching Senet...
java -cp bin senet.Senet

endlocal
