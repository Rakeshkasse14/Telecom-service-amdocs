@echo off
echo ====================================================
echo   Compiling Telecom Service Assurance System (TSATMS)
echo ====================================================

if not exist bin mkdir bin

javac -cp "lib/*" -d bin src/com/amdocs/telecom/enums/*.java src/com/amdocs/telecom/model/*.java src/com/amdocs/telecom/exception/*.java src/com/amdocs/telecom/dto/*.java src/com/amdocs/telecom/security/*.java src/com/amdocs/telecom/util/*.java src/com/amdocs/telecom/pattern/*.java src/com/amdocs/telecom/dao/*.java src/com/amdocs/telecom/dao/impl/*.java src/com/amdocs/telecom/service/*.java src/com/amdocs/telecom/service/impl/*.java src/com/amdocs/telecom/scheduler/*.java src/com/amdocs/telecom/report/*.java src/com/amdocs/telecom/main/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation Successful!
) else (
    echo Compilation Failed!
)
