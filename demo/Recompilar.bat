@echo off
rem Usar despues de modificar el codigo (por ejemplo, al agregar un usuario nuevo).
rem Ejecutar.bat no vuelve a compilar solo si el .jar ya existe, asi que hay que
rem forzarlo con este script cuando cambia algo.
setlocal
cd /d "%~dp0"

echo Compilando proyecto...
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Error al compilar el proyecto.
    pause
    exit /b 1
)

echo.
echo Listo. Ahora podes abrir la app con Ejecutar.bat
pause
