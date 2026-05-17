# Bauti TV - Native Android Application

Una aplicación de TV nativa para Android con soporte completo para IPTV, HLS y streaming de video.

## Características

✅ **100% Nativo** - No WebView, código Kotlin puro
✅ **ExoPlayer** - Reproductor de video profesional
✅ **Control Remoto TV** - Interfaz de control remoto completa
✅ **Soporte HLS** - Reproducción de streams M3U8
✅ **Aislamiento de Seguridad** - Sin redirecciones externas en el player
✅ **Material Design 3** - Interfaz moderna y responsiva
✅ **API 24+** - Compatible con Android 7.0+

## Requisitos Previos

- **Android Studio** (última versión)
- **Android SDK 34**
- **JDK 11 o superior**
- **Gradle 8.0+**

## Instalación del SDK

1. Abre Android Studio
2. Ve a `Tools` → `SDK Manager`
3. Instala:
   - Android SDK 34
   - Build Tools 34.0.0
   - Android Emulator (opcional)

## Compilación

### Opción 1: Android Studio (Recomendado)

```bash
1. Abre el proyecto en Android Studio
2. Espera a que Gradle se sincronice
3. Ejecuta: Build → Build Bundle(s) / APK(s) → Build APK(s)
```

### Opción 2: Desde Terminal

```bash
# Compilar APK de debug
./gradlew assembleDebug

# Compilar APK de release (optimizado)
./gradlew assembleRelease

# Compilar Bundle (para Google Play)
./gradlew bundleRelease
```

## Ubicación de Outputs

- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`
- **Bundle**: `app/build/outputs/bundle/release/app-release.aab`

## Instalación en Dispositivo

### Desde Android Studio
```
Run → Run 'app'
```

### Desde Terminal
```bash
# Instalar APK debug
./gradlew installDebug

# O manualmente
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Arquitectura del Proyecto

```
BautiTV_Native/
├── app/
│   ├── src/main/
│   │   ├── java/com/bautiapp/tv/
│   │   │   ├── MainActivity.kt           # Pantalla principal
│   │   │   ├── VideoPlayerActivity.kt    # Reproductor de video
│   │   │   ├── Channel.kt                # Modelo de datos
│   │   │   ├── data/
│   │   │   │   ├── ChannelRepository.kt  # Gestión de canales
│   │   │   │   └── RemoteController.kt   # Lógica del control remoto
│   │   │   └── ui/
│   │   │       └── ChannelsAdapter.kt    # Adaptador de canales
│   │   ├── res/
│   │   │   ├── layout/                   # XML de interfaces
│   │   │   ├── drawable/                 # Iconos y estilos
│   │   │   └── values/                   # Colores, strings, estilos
│   │   └── AndroidManifest.xml
│   └── build.gradle                      # Dependencias y configuración
├── build.gradle                          # Configuración global
├── settings.gradle                       # Módulos del proyecto
├── gradle.properties                     # Propiedades de Gradle
└── local.properties                      # Configuración local

```

## Funcionalidades Principales

### 1. Pantalla Principal
- Grid de canales con logos
- Búsqueda y filtrado
- Control remoto virtual
- Carga dinámica de canales

### 2. Reproductor de Video
- Soporte HLS y MP4
- Controles nativos de ExoPlayer
- Indicador de carga
- Manejo de errores
- Sin WebView ni redirecciones externas

### 3. Control Remoto Virtual
- D-Pad (Arriba/Abajo/Izquierda/Derecha)
- Botón OK para seleccionar
- Control de volumen
- Soporte para mandos Bluetooth

### 4. Seguridad
- No permite redirecciones externas en el player
- Aislamiento de contenido
- Sin acceso a navegador web desde el reproductor

## Personalización

### Cambiar Colores
Edita `app/src/main/res/values/colors.xml`

### Cambiar Fuentes
Agrega archivos `.ttf` en `app/src/main/res/font/`

### Configurar API de Canales
Edita la URL en `app/src/main/java/com/bautiapp/tv/data/ChannelRepository.kt`

```kotlin
val url = "https://tu-api.com/channels.json"
```

## Estructura de Respuesta API

La API debe retornar JSON con esta estructura:

```json
{
  "groups": {
    "News": [
      {
        "name": "BBC News",
        "url": "https://example.com/stream.m3u8",
        "logo": "https://example.com/bbc.png",
        "group": "News"
      }
    ],
    "Sports": [
      {
        "name": "Sky Sports",
        "url": "https://example.com/sports.m3u8",
        "logo": "https://example.com/sky.png",
        "group": "Sports"
      }
    ]
  },
  "total": 2
}
```

## Firma Digital (Release)

Para generar APK de release firmado:

1. Crea una keystore:
```bash
keytool -genkey -v -keystore bautikey.jks -keyalg RSA -keysize 2048 -validity 10000
```

2. Edita `gradle.properties`:
```properties
STORE_FILE=bautikey.jks
STORE_PASSWORD=tu_password
KEY_ALIAS=bautikey
KEY_PASSWORD=tu_password
```

3. En `app/build.gradle`, agrega en `android` block:
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
    }
}
```

4. Compila:
```bash
./gradlew assembleRelease
```

## Troubleshooting

### Error: "SDK not found"
→ Configura la ruta del SDK en `local.properties`:
```
sdk.dir=/Users/username/Library/Android/sdk
```

### Error: "Gradle sync failed"
→ Ejecuta:
```bash
./gradlew clean
./gradlew sync
```

### APK muy grande
→ Ejecuta release con minificación:
```bash
./gradlew assembleRelease
```

### Problemas de permisos
→ Verifica `AndroidManifest.xml` que tenga:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Dependencias Principales

- **ExoPlayer 2.19.1** - Reproductor de video profesional
- **OkHttp 4.11** - Cliente HTTP
- **GSON 2.10.1** - Parsing JSON
- **Picasso 2.8** - Carga de imágenes
- **Jetpack** - AndroidX y componentes modernos

## Licencia

Bauti TV © 2024

## Soporte

Para reportar errores o solicitar features:
1. Verifica que tengas la última versión
2. Proporciona logs de error
3. Describe pasos para reproducir

---

**Última actualización**: May 17, 2024
**Versión**: 1.0.0
