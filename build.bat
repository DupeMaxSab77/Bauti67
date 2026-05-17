@echo off
REM BautiTV Build Script for Windows
REM Script para compilar la aplicación automáticamente

setlocal enabledelayedexpansion

echo ================================
echo   Bauti TV - Native Build Tool  
echo ================================
echo.

REM Verificar si estamos en el directorio correcto
if not exist "build.gradle" (
    echo Error: build.gradle no encontrado
    echo Asegúrate de estar en el directorio raíz del proyecto
    exit /b 1
)

REM Menu
echo Selecciona una opción:
echo 1) Compilar APK Debug
echo 2) Compilar APK Release
echo 3) Instalar APK Debug en dispositivo
echo 4) Limpiar y recompilar
echo 5) Compilar Bundle para Play Store
echo 6) Ver dispositivos conectados
echo.
set /p option="Opción (1-6): "

if "%option%"=="1" (
    echo Compilando APK Debug...
    call gradlew.bat assembleDebug
    echo.
    echo APK Debug compilado en: app\build\outputs\apk\debug\app-debug.apk
) else if "%option%"=="2" (
    echo Compilando APK Release...
    call gradlew.bat assembleRelease
    echo.
    echo APK Release compilado en: app\build\outputs\apk\release\app-release.apk
) else if "%option%"=="3" (
    echo Instalando APK Debug...
    call gradlew.bat installDebug
    echo.
    echo APK instalado correctamente
) else if "%option%"=="4" (
    echo Limpiando proyecto...
    call gradlew.bat clean
    echo Recompilando...
    call gradlew.bat assembleDebug
    echo.
    echo Proyecto limpiado y recompilado
) else if "%option%"=="5" (
    echo Compilando Bundle para Play Store...
    call gradlew.bat bundleRelease
    echo.
    echo Bundle compilado en: app\build\outputs\bundle\release\app-release.aab
) else if "%option%"=="6" (
    echo Dispositivos conectados:
    adb devices -l
) else (
    echo Opción inválida
    exit /b 1
)

echo.
echo ¡Listo!
pause
