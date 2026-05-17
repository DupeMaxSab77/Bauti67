# 🚀 Guía Rápida de Compilación - Bauti TV Native

## ⚡ Inicio Rápido (5 minutos)

### Opción 1: Android Studio (MÁS FÁCIL)

1. **Descarga e instala Android Studio**
   - https://developer.android.com/studio

2. **Abre el proyecto**
   - File → Open → Selecciona la carpeta `BautiTV_Native`

3. **Espera la sincronización de Gradle**
   - Android Studio descargará todas las dependencias automáticamente

4. **Compila el APK**
   - Menu superior: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`

5. **Resultado**
   - El APK estará en: `app/build/outputs/apk/debug/app-debug.apk`

---

### Opción 2: Terminal (Para usuarios avanzados)

**Windows:**
```bash
cd BautiTV_Native
.\build.bat
# Selecciona opción 1 o 2
```

**Mac/Linux:**
```bash
cd BautiTV_Native
chmod +x build.sh
./build.sh
# Selecciona opción 1 o 2
```

---

## 📋 Requisitos

| Requisito | Versión Mínima |
|-----------|-----------------|
| Android Studio | 2022.3.1+ |
| Android SDK | API 34 |
| JDK | 11+ |
| RAM disponible | 4GB |
| Espacio disco | 2GB |

---

## 🔧 Configuración Inicial (Primera Vez)

### 1. Configura el SDK

Si no detecta el SDK automáticamente:

```bash
# En la raíz del proyecto, edita o crea local.properties:
sdk.dir=/path/to/android/sdk

# Ejemplos:
# Windows:
# sdk.dir=C:\\Users\\Username\\AppData\\Local\\Android\\sdk

# Mac:
# sdk.dir=/Users/username/Library/Android/sdk

# Linux:
# sdk.dir=/home/username/Android/sdk
```

### 2. Asigna más memoria a Gradle

Edita `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=512m
```

---

## 📱 Compilación Rápida

### APK Debug (Para pruebas)
```bash
./gradlew assembleDebug
# O en Windows: .\gradlew.bat assembleDebug
```
**Ubicación**: `app/build/outputs/apk/debug/app-debug.apk`

### APK Release (Para distribución)
```bash
./gradlew assembleRelease
```
**Ubicación**: `app/build/outputs/apk/release/app-release.apk`

### Bundle AAB (Para Google Play)
```bash
./gradlew bundleRelease
```
**Ubicación**: `app/build/outputs/bundle/release/app-release.aab`

---

## 📤 Instalar en Dispositivo

### Requisito: Dispositivo conectado con USB o emulador corriendo

```bash
# Instalar APK Debug automáticamente
./gradlew installDebug

# O manualmente
adb install app/build/outputs/apk/debug/app-debug.apk

# Ejecutar directamente desde Android Studio
Run → Run 'app' (Ctrl+R)
```

---

## 🐛 Solucionar Errores Comunes

### Error: "SDK not found"
```bash
# Instala el SDK
# Android Studio → Tools → SDK Manager → Install Android SDK 34
# O configura en local.properties
```

### Error: "Gradle sync failed"
```bash
./gradlew clean
./gradlew sync
```

### Error: "Compilation failed"
```bash
# Limpia cache
./gradlew clean
# Recompila
./gradlew assembleDebug
```

### APK muy grande
```bash
# Usa release (optimizado)
./gradlew assembleRelease
```

### No detecta dispositivo
```bash
# Reinicia el servicio ADB
adb kill-server
adb start-server
adb devices
```

---

## 📊 Estructura de Compilación

```
gradle build process:
├── Compile Kotlin code
├── Process resources
├── Dex compilation
├── Package APK
└── Sign & Align (Release)
```

**Tiempo estimado**: 1-3 minutos la primera vez, 30-60 segundos después

---

## ✨ Tips de Optimización

1. **Build más rápidas**: Habilita "Instant Run"
   - Settings → Compiler → ✓ Enable Instant Run

2. **Menos uso de RAM**: Reduce el heap
   - gradle.properties: `org.gradle.jvmargs=-Xmx2048m`

3. **Compilación offline**: Descarga dependencias
   - `./gradlew --offline assembleDebug`

4. **Compilación paralela**: Actívala
   - gradle.properties: `org.gradle.parallel=true`

---

## 🎯 Próximos Pasos

1. ✅ Compila el APK
2. 📦 Instala en dispositivo
3. 🧪 Prueba la app
4. 🔧 Personaliza código en:
   - `app/src/main/java/com/bautiapp/tv/`
5. 📝 Modifica UI en:
   - `app/src/main/res/layout/`
6. 🚀 Recompila con cambios

---

## 📞 Soporte

Si hay errores durante la compilación:

1. Copia el error completo
2. Busca en Google/Stack Overflow
3. Verifica que tengas:
   - ✓ Java/JDK 11+
   - ✓ Android SDK 34
   - ✓ Conexión a internet
   - ✓ Suficiente espacio en disco

---

## 🔐 Para Distribución en Google Play

1. **Genera clave firmada**
   ```bash
   keytool -genkey -v -keystore bautikey.jks \
     -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Configura en `gradle.properties`**
   ```properties
   STORE_FILE=bautikey.jks
   STORE_PASSWORD=tu_contraseña
   KEY_ALIAS=bautikey
   KEY_PASSWORD=tu_contraseña
   ```

3. **Compila Bundle firmado**
   ```bash
   ./gradlew bundleRelease
   ```

4. **Sube a Google Play Console**

---

**¡Listo para compilar! 🎉**

Ejecuta: `./gradlew assembleDebug` y disfruta tu app nativa en Android.
