@echo off
setlocal
title Snake Game - Instalador

echo.
echo  ==========================================
echo    SNAKE GAME - Descargando e iniciando...
echo  ==========================================
echo.

:: Verificar que Java esta instalado
java -version >nul 2>&1
if errorlevel 1 (
    echo  ERROR: Java no esta instalado.
    echo  Descargalo desde: https://www.java.com/es/download/
    echo.
    pause
    exit /b 1
)

:: Directorio donde se instalara el juego
set INSTALL_DIR=%USERPROFILE%\SnakeGame

:: Si ya existe, preguntar si reinstalar
if exist "%INSTALL_DIR%" (
    echo  El juego ya esta instalado en: %INSTALL_DIR%
    echo.
    set /p RESP= Reinstalar? (s/n):
    if /i "%RESP%"=="s" (
        rmdir /s /q "%INSTALL_DIR%"
    ) else (
        goto :ejecutar
    )
)

:: Descargar zip del repositorio de GitHub
echo  Descargando juego desde GitHub...
curl -L -o "%TEMP%\SnakeGame.zip" "https://github.com/Mariodday/SnakeGame/archive/refs/heads/main.zip"
if errorlevel 1 (
    echo  ERROR: No se pudo descargar. Verifica tu conexion a internet.
    pause
    exit /b 1
)

:: Extraer el zip
echo  Extrayendo archivos...
powershell -Command "Expand-Archive -Path '%TEMP%\SnakeGame.zip' -DestinationPath '%TEMP%\SnakeGameExtract' -Force"
move "%TEMP%\SnakeGameExtract\SnakeGame-main" "%INSTALL_DIR%" >nul
del "%TEMP%\SnakeGame.zip" >nul
rmdir /s /q "%TEMP%\SnakeGameExtract" >nul

echo  Instalacion completada en: %INSTALL_DIR%
echo.

:ejecutar
set JAR=%INSTALL_DIR%\SnakeGame\dist\SnakeGame.jar
set LIBS=%INSTALL_DIR%\SnakeGame\dist\lib

if not exist "%JAR%" (
    echo  ERROR: No se encontro el archivo del juego.
    pause
    exit /b 1
)

echo  Iniciando Snake Game...
java --module-path "%LIBS%" --add-modules javafx.controls,javafx.graphics,javafx.base -jar "%JAR%"

endlocal
