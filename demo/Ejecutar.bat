@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

set "JAR=target\demo-0.0.1-SNAPSHOT.jar"

rem Usar javaw del mismo JDK que Maven (via JAVA_HOME) si esta disponible,
rem porque "javaw" solo puede no estar en el PATH aunque "java" si lo este.
set "JAVAW=javaw"
if defined JAVA_HOME set "JAVAW=%JAVA_HOME%\bin\javaw.exe"

if not exist "%JAR%" (
    echo Primera vez que la ejecutas: compilando el proyecto, un momento...
    call mvnw.cmd -q clean package -DskipTests
    if !ERRORLEVEL! NEQ 0 (
        echo.
        echo Error al compilar el proyecto. Revisa el mensaje de arriba.
        pause
        exit /b 1
    )
)

start "" "%JAVAW%" -jar "%JAR%"
exit
