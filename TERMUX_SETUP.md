# Compilar en Termux - Guía Paso a Paso

## 1. Instalar dependencias

```bash
pkg update && pkg upgrade
pkg install gradle openjdk-21
```

## 2. Configurar ANDROID_HOME (SDK de Termux)

```bash
# El SDK de android-tools de Termux está en:
export ANDROID_HOME=$PREFIX
export PATH=$PATH:$ANDROID_HOME/bin
```

## 3. Compilar el APK

```bash
# En la carpeta del proyecto:
cd ~/storage/downloads/BautiTV_Native

# Opción A: usar gradle directamente (recomendado en Termux)
gradle assembleDebug

# Opción B: usar el wrapper
chmod +x gradlew
./gradlew assembleDebug
```

## 4. APK resultante

```
app/build/outputs/apk/debug/app-debug.apk
```

## Si hay error de SDK no encontrado

Crea o edita `local.properties` en la raíz del proyecto:

```bash
echo "sdk.dir=$PREFIX" > local.properties
```

## Si hay error de memoria

```bash
export GRADLE_OPTS="-Xmx1g"
gradle assembleDebug
```

## Si hay error de Java version

```bash
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
gradle assembleDebug
```
