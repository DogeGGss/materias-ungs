@echo off
setlocal
cd /d "%~dp0"

set JAR=target\demo-0.0.1-SNAPSHOT.jar

if not exist "%JAR%" (
    echo Primera vez que la ejecutas: compilando el proyecto, un momento...
    call mvnw.cmd -q clean package -DskipTests
    if %ERRORLEVEL% NEQ 0 (
        echo.
        echo Error al compilar el proyecto.
        pause
        exit /b 1
    )
)

start "" javaw -jar "%JAR%"
exit
