
$BASE    = Split-Path $MyInvocation.MyCommand.Path
$FX_DIR  = (Get-ChildItem "$BASE" -Directory | Where-Object { $_.Name -like "javafx-sdk*" } | Sort-Object Name -Descending | Select-Object -First 1).FullName
if (-not $FX_DIR) { $FX_DIR = "$BASE\javafx-sdk" }
$FX_LIB  = "$FX_DIR\lib"
$OUT_DIR = "$BASE\out"
$SRC_DIR = "$BASE\SnakeGame\src"

# URL de JavaFX 21 LTS para Windows x64
$FX_URL  = "https://download2.gluonhq.com/openjfx/21.0.7/openjfx-21.0.7_windows-x64_bin-sdk.zip"
$FX_ZIP  = "$BASE\javafx.zip"

Write-Host ""
Write-Host "  ==========================================" -ForegroundColor Cyan
Write-Host "    SNAKE GAME - Launcher" -ForegroundColor Cyan
Write-Host "  ==========================================" -ForegroundColor Cyan
Write-Host ""

if (-not (Test-Path "$FX_LIB\javafx.controls.jar")) {
    Write-Host "  [1/3] JavaFX no encontrado. Descargando..." -ForegroundColor Yellow
    Write-Host "        $FX_URL" -ForegroundColor DarkGray

    try {
        $ProgressPreference = 'SilentlyContinue'
        Invoke-WebRequest -Uri $FX_URL -OutFile $FX_ZIP -UseBasicParsing
        Write-Host "  [1/3] Extrayendo..." -ForegroundColor Yellow
        Expand-Archive -Path $FX_ZIP -DestinationPath $BASE -Force
        # El zip extrae a openjfx-21.0.7_windows-x64
        $extracted = Get-ChildItem "$BASE" -Directory | Where-Object { $_.Name -like "javafx-sdk*" } | Select-Object -First 1
        if ($extracted) {
            Rename-Item $extracted.FullName "$FX_DIR" -Force -ErrorAction SilentlyContinue
        }
        Remove-Item $FX_ZIP -ErrorAction SilentlyContinue
        Write-Host "  [1/3] JavaFX listo." -ForegroundColor Green
    } catch {
        Write-Host ""
        Write-Host "  ERROR: No se pudo descargar JavaFX." -ForegroundColor Red
        Write-Host "  Descargalo manualmente desde: https://gluonhq.com/products/javafx/" -ForegroundColor Red
        Write-Host "  y extrae la carpeta como: $FX_DIR" -ForegroundColor Red
        Write-Host ""
        Read-Host "  Presiona Enter para salir"
        exit 1
    }
} else {
    Write-Host "  [1/3] JavaFX encontrado." -ForegroundColor Green
}

Write-Host "  [2/3] Compilando..." -ForegroundColor Yellow
if (-not (Test-Path $OUT_DIR)) { New-Item -ItemType Directory -Path $OUT_DIR | Out-Null }

$sources = @(
    "$SRC_DIR\module-info.java"
) + (Get-ChildItem "$SRC_DIR\serpiente\*.java" | ForEach-Object { $_.FullName })

$compileArgs = @(
    "--module-path", $FX_LIB,
    "--add-modules", "javafx.controls,javafx.graphics",
    "-d", $OUT_DIR
) + $sources

& javac @compileArgs 2>&1 | ForEach-Object { Write-Host "  $_" -ForegroundColor DarkGray }

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "  ERROR: Fallo la compilacion." -ForegroundColor Red
    Write-Host ""
    Read-Host "  Presiona Enter para salir"
    exit 1
}
Write-Host "  [2/3] Compilacion exitosa." -ForegroundColor Green

Write-Host "  [3/3] Iniciando juego..." -ForegroundColor Yellow
Write-Host ""

& java --module-path "$FX_LIB;$OUT_DIR" -m serpiente/serpiente.Principal
