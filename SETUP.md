# 📦 Bauti TV Native - Instrucciones de Instalación y Compilación

## ✅ Lo que hemos creado

Una **aplicación Android nativa 100%** con:
- ✅ Kotlin puro (Sin WebView)
- ✅ ExoPlayer para reproducción HLS/MP4
- ✅ Interfaz TV moderna
- ✅ Control remoto virtual
- ✅ Seguridad de aislamiento
- ✅ Compilable directamente
- ✅ API 24+ (Android 7.0+)

---

## 🎯 PASOS PARA COMPILAR

### PASO 1: Descargar e Instalar Requisitos

#### Opción A: Windows
1. **Descarga Android Studio**
   - https://developer.android.com/studio
   - Ejecuta el instalador
   - Selecciona: Android Studio + SDK

2. **Instala Java/JDK** (si no lo tienes)
   - https://www.oracle.com/java/technologies/downloads/
   - O usa el JDK incluido en Android Studio

3. **Configura variables de entorno**
   - JAVA_HOME → ruta del JDK
   - ANDROID_SDK_ROOT → ruta del SDK

#### Opción B: macOS
```bash
# Instala con Homebrew (recomendado)
brew install --cask android-studio openjdk

# O descarga manualmente
# https://developer.android.com/studio
```

#### Opción C: Linux
```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install android-studio default-jdk

# Fedora
sudo dnf install android-studio java-latest-openjdk
```

---

### PASO 2: Extrae el Proyecto

```bash
# Windows
# Descarga BautiTV_Native.zip
# Click derecho → Extraer aquí
# O usa 7-Zip/WinRAR

# macOS/Linux
unzip BautiTV_Native.zip
cd BautiTV_Native
```

---

### PASO 3: Abre en Android Studio

1. **Inicia Android Studio**
2. **File → Open**
3. **Selecciona la carpeta `BautiTV_Native`**
4. **Espera a que cargue (1-5 minutos)**
5. **Gradle se sincronizará automáticamente**

⚠️ **Si aparece error de SDK:**
- Tools → SDK Manager
- Instala: Android SDK 34
- Haz click en "Apply"

---

### PASO 4: Compila el APK

#### Método A: Android Studio UI (MÁS FÁCIL)
```
Menu Superior:
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

Espera 2-3 minutos...

✅ **Cuando termine, verá notificación de éxito**

#### Método B: Terminal
```bash
# Navega a la carpeta del proyecto
cd BautiTV_Native

# Windows
.\gradlew.bat assembleDebug

# macOS/Linux
./gradlew assembleDebug
```

#### Método C: Script Interactivo
```bash
# Windows
build.bat
# Selecciona opción 1

# macOS/Linux
./build.sh
# Selecciona opción 1
```

---

### PASO 5: Encuentra el APK Compilado

**Ubicación del archivo:**

```
BautiTV_Native/app/build/outputs/apk/debug/app-debug.apk
```

O en Android Studio:
```
Build → Build Analyzer → Mostrar en carpeta
```

---

### PASO 6: Instala en Dispositivo

#### Opción 1: Android Studio (Si tienes dispositivo/emulador conectado)
```
Run → Run 'app'
```

#### Opción 2: Conectar dispositivo USB
1. **Habilita "USB Debugging"** en el teléfono
   - Configuración → Opciones del Desarrollador → USB Debugging
2. **Conecta el teléfono por USB**
3. **Android Studio detectará automáticamente**
4. **Click en Run**

#### Opción 3: Instalar manualmente
```bash
# Copia el APK a tu dispositivo
# O instala vía terminal
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🛠️ Troubleshooting (Solución de Problemas)

### ❌ "SDK not found"
**Solución:**
```
Android Studio → Tools → SDK Manager → Install Android SDK 34
```

### ❌ "Gradle sync failed"
**Solución:**
```bash
cd BautiTV_Native
./gradlew clean
./gradlew sync
```

### ❌ "File not found: build.gradle"
**Solución:**
- Asegúrate de estar en la carpeta `BautiTV_Native`
- No en el archivo ZIP extraído incorrectamente

### ❌ "No connected devices"
**Solución:**
```bash
# Reinicia ADB
adb kill-server
adb start-server

# O instala drivers USB para tu dispositivo
```

### ❌ "Build took too long"
**Solución:**
- Asigna más RAM en `gradle.properties`:
  ```
  org.gradle.jvmargs=-Xmx4096m
  ```

### ❌ "Cannot find symbol 'MainActivity'"
**Solución:**
```bash
./gradlew clean
./gradlew build
```

---

## 📊 Versiones de Compilación

### Debug APK (Para desarrollo)
- Más rápida de compilar
- Tamaño más grande (~30-40MB)
- Sin optimización
- Contiene símbolos de depuración

**Comando:**
```bash
./gradlew assembleDebug
```

### Release APK (Para distribución)
- Optimizada y minificada
- Tamaño más pequeño (~15-20MB)
- Sin información de depuración
- Más rápida en ejecución

**Comando:**
```bash
./gradlew assembleRelease
```

---

## 🚀 Para Google Play Store

### 1. Genera clave de firma

```bash
keytool -genkey -v -keystore bautikey.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias bautikey
```

### 2. Configura en `gradle.properties`

```properties
STORE_FILE=bautikey.jks
STORE_PASSWORD=tu_contraseña
KEY_ALIAS=bautikey
KEY_PASSWORD=tu_contraseña
```

### 3. Edita `app/build.gradle`

Agrega en el bloque `android`:

```gradle
signingConfigs {
    release {
        storeFile file(STORE_FILE)
        storePassword STORE_PASSWORD
        keyAlias KEY_ALIAS
        keyPassword KEY_PASSWORD
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
        minifyEnabled true
        shrinkResources true
    }
}
```

### 4. Compila Bundle

```bash
./gradlew bundleRelease
```

Ubica en: `app/build/outputs/bundle/release/app-release.aab`

---

## 📝 Estructura del Proyecto Entregado

```
BautiTV_Native/
├── 📄 README.md               ← Lee esto primero
├── 📄 QUICK_START.md          ← Guía rápida
├── 📄 ARCHITECTURE.md         ← Documentación técnica
├── build.sh / build.bat       ← Scripts de compilación
├── app/
│   ├── build.gradle           ← Dependencias
│   ├── proguard-rules.pro     ← Minificación
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/bautiapp/tv/
│   │   │   ├── MainActivity.kt           ✅ Pantalla principal
│   │   │   ├── VideoPlayerActivity.kt    ✅ Reproductor ExoPlayer
│   │   │   ├── AppConfig.kt              ✅ Configuración
│   │   │   ├── data/
│   │   │   │   ├── ChannelRepository.kt  ✅ API de canales
│   │   │   │   └── RemoteController.kt   ✅ Control remoto
│   │   │   └── ui/
│   │   │       └── ChannelsAdapter.kt    ✅ RecyclerView Adapter
│   │   └── res/
│   │       ├── layout/        ✅ Layouts XML
│   │       ├── drawable/      ✅ Iconos vectoriales
│   │       ├── values/        ✅ Strings y estilos
│   │       └── xml/           ✅ Configuración
│   └── build/outputs/apk/    ← 📦 APK compilado aquí
├── gradle/                    ← Gradle Wrapper
├── build.gradle
├── settings.gradle
├── gradle.properties
└── local.properties
```

---

## 🎨 Personalización Rápida

### Cambiar nombre de la app
Edita: `app/src/main/res/values/strings.xml`
```xml
<string name="app_name">Mi App TV</string>
```

### Cambiar URL de API
Edita: `app/src/main/java/com/bautiapp/tv/AppConfig.kt`
```kotlin
const val API_BASE_URL = "https://tu-api.com/"
```

### Cambiar colores
Edita: `app/src/main/res/values/colors.xml`
```xml
<color name="primary_red">#tu_color</color>
```

### Cambiar icono de la app
Coloca PNG en: `app/src/main/res/mipmap-*/ic_launcher.png`

---

## ✨ Características Implementadas

| Característica | Estado |
|---|---|
| Reproducción HLS | ✅ Nativa |
| Reproducción MP4 | ✅ Nativa |
| Control Remoto | ✅ D-Pad + Botones |
| Búsqueda | ✅ Filtrado en tiempo real |
| Categorías | ✅ Agrupadas |
| Seguridad | ✅ Sin redirecciones |
| Material Design | ✅ Tema oscuro |
| TV Mode | ✅ Interfaz remota |
| Offline Cache | ⏳ Futuro |
| Analytics | ⏳ Futuro |

---

## 📞 Soporte Quick Links

### Documentos en el proyecto:
- `README.md` - Documentación completa
- `QUICK_START.md` - Inicio rápido
- `ARCHITECTURE.md` - Detalles técnicos

### Recursos oficiales:
- [Android Studio Help](https://developer.android.com/studio)
- [ExoPlayer Docs](https://exoplayer.dev)
- [Kotlin Documentation](https://kotlinlang.org/docs)
- [Android Gradle Plugin](https://developer.android.com/build)

---

## 🎉 ¡Listo!

Con estos pasos:

1. ✅ Tienes un proyecto Android profesional
2. ✅ 100% Nativo (sin WebView)
3. ✅ Compilable directamente
4. ✅ Con documentación completa
5. ✅ Listo para distribuir

**Próximo paso:** `./gradlew assembleDebug` 🚀

---

## 📋 Checklist de Verificación

Antes de compilar, verifica:

- [ ] Java JDK 11+ instalado
- [ ] Android SDK 34 instalado
- [ ] Android Studio abierto
- [ ] Proyecto sincronizado (sin errores)
- [ ] Suficiente espacio en disco (2GB)
- [ ] Conexión a internet (para descargar dependencias)

---

**Versión: 1.0.0**
**Fecha: May 17, 2024**
**Estado: ✅ Listo para Producción**
