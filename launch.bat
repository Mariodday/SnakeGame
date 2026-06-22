@echo off
setlocal

:: ============================================================
:: SNAKE GAME - Launcher (Windows)
:: Ajusta FX_PATH si tu JavaFX SDK esta en otra ubicacion
:: ============================================================

:: --- Ruta al SDK de JavaFX (edita esto si es necesario) ---
set FX_PATH=C:\javafx-sdk\lib

:: Si existe una variable de entorno PATH_FX definida por el usuario, usarla
if defined PATH_FX set FX_PATH=%PATH_FX%

:: Verificar que JavaFX existe
if not exist "%FX_PATH%" (
    echo.
    echo  ERROR: No se encontro JavaFX SDK en: %FX_PATH%
    echo.
    echo  Descarga JavaFX desde: https://gluonhq.com/products/javafx/
    echo  Luego edita este archivo y cambia FX_PATH a la ruta correcta.
    echo.
    pause
    exit /b 1
)

:: Directorio base del script
set BASE_DIR=%~dp0SnakeGame
set OUT_DIR=%~dp0out

:: Crear carpeta de salida
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

echo.
echo  ==========================================
echo    SNAKE GAME - Compilando...
echo  ==========================================
echo.

javac --module-path "%FX_PATH%" ^
      --add-modules javafx.controls,javafx.graphics ^
      -d "%OUT_DIR%" ^
      "%BASE_DIR%\src\module-info.java" ^
      "%BASE_DIR%\src\serpiente\*.java"

if errorlevel 1 (
    echo.
    echo  ERROR: Fallo la compilacion. Revisa que tengas JDK 17+ instalado.
    echo.
    pause
    exit /b 1
)

echo.
echo  ==========================================
echo    SNAKE GAME - Iniciando juego...
echo  ==========================================
echo.

java --module-path "%FX_PATH%;%OUT_DIR%" -m serpiente/serpiente.Principal

endlocal
