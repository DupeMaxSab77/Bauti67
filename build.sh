#!/bin/bash

# BautiTV Build Script
# Script para compilar la aplicación automáticamente

set -e

echo "================================"
echo "  Bauti TV - Native Build Tool  "
echo "================================"
echo ""

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar si estamos en el directorio correcto
if [ ! -f "build.gradle" ]; then
    echo -e "${RED}Error: build.gradle no encontrado${NC}"
    echo "Asegúrate de estar en el directorio raíz del proyecto"
    exit 1
fi

# Menu
echo "Selecciona una opción:"
echo "1) Compilar APK Debug"
echo "2) Compilar APK Release"
echo "3) Instalar APK Debug en dispositivo"
echo "4) Limpiar y recompilar"
echo "5) Compilar Bundle para Play Store"
echo "6) Ver dispositivos conectados"
echo ""
read -p "Opción (1-6): " option

case $option in
    1)
        echo -e "${YELLOW}Compilando APK Debug...${NC}"
        ./gradlew assembleDebug
        echo -e "${GREEN}✓ APK Debug compilado en: app/build/outputs/apk/debug/app-debug.apk${NC}"
        ;;
    2)
        echo -e "${YELLOW}Compilando APK Release...${NC}"
        ./gradlew assembleRelease
        echo -e "${GREEN}✓ APK Release compilado en: app/build/outputs/apk/release/app-release.apk${NC}"
        ;;
    3)
        echo -e "${YELLOW}Instalando APK Debug...${NC}"
        ./gradlew installDebug
        echo -e "${GREEN}✓ APK instalado correctamente${NC}"
        ;;
    4)
        echo -e "${YELLOW}Limpiando proyecto...${NC}"
        ./gradlew clean
        echo -e "${YELLOW}Recompilando...${NC}"
        ./gradlew assembleDebug
        echo -e "${GREEN}✓ Proyecto limpiado y recompilado${NC}"
        ;;
    5)
        echo -e "${YELLOW}Compilando Bundle para Play Store...${NC}"
        ./gradlew bundleRelease
        echo -e "${GREEN}✓ Bundle compilado en: app/build/outputs/bundle/release/app-release.aab${NC}"
        ;;
    6)
        echo -e "${YELLOW}Dispositivos conectados:${NC}"
        adb devices -l
        ;;
    *)
        echo -e "${RED}Opción inválida${NC}"
        exit 1
        ;;
esac

echo ""
echo -e "${GREEN}¡Listo!${NC}"
