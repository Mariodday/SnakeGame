@echo off
setlocal
title Snake Game - Instalador

echo.
echo  ==========================================
echo    SNAKE GAME - Descargando e iniciando...
echo  ==========================================
echo.

:: Verificar que Java (JDK) esta instalado
javac -version >nul 2>&1
if errorlevel 1 (
    echo  ERROR: Java JDK no esta instalado.
    echo  Descargalo desde: https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

set INSTALL_DIR=%USERPROFILE%\SnakeGame

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

:: Compilar el codigo fuente
echo  Compilando el juego...
set SRC=%INSTALL_DIR%\SnakeGame\src
set OUT=%INSTALL_DIR%\out
set LIBS=%INSTALL_DIR%\SnakeGame\dist\lib

if not exist "%OUT%" mkdir "%OUT%"

javac --module-path "%LIBS%" ^
      --add-modules javafx.controls,javafx.graphics,javafx.base ^
      -d "%OUT%" ^
      "%SRC%\module-info.java" ^
      "%SRC%\serpiente\*.java"

if errorlevel 1 (
    echo  ERROR: Fallo la compilacion.
    pause
    exit /b 1
)

echo  Instalacion completada.
echo.

:ejecutar
set OUT=%INSTALL_DIR%\out
set LIBS=%INSTALL_DIR%\SnakeGame\dist\lib

if not exist "%OUT%" (
    echo  ERROR: No se encontro el juego compilado.
    pause
    exit /b 1
)

echo  Iniciando Snake Game...
java --module-path "%LIBS%;%OUT%" ^
     --add-modules javafx.controls,javafx.graphics,javafx.base ^
     -m serpiente/serpiente.Principal

endlocal
