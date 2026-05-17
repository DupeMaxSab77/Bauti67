# 🏗️ Arquitectura de Bauti TV Native

## Visión General

Bauti TV es una aplicación **100% nativa en Kotlin** para Android que proporciona un reproductor de IPTV con interfaz TV moderna.

```
┌─────────────────────────────────────────────┐
│        Presentation Layer (UI)               │
│  ├─ MainActivity                             │
│  ├─ VideoPlayerActivity                      │
│  └─ ChannelsAdapter                          │
├─────────────────────────────────────────────┤
│        Domain Layer (Business Logic)          │
│  ├─ RemoteController                         │
│  └─ Channel Data Model                       │
├─────────────────────────────────────────────┤
│        Data Layer (Repository)                │
│  ├─ ChannelRepository                        │
│  └─ OkHttp Client                            │
├─────────────────────────────────────────────┤
│        Media Layer (ExoPlayer)                │
│  ├─ Native HLS Support                       │
│  ├─ Adaptive Bitrate                         │
│  └─ Buffer Management                        │
└─────────────────────────────────────────────┘
```

## Componentes Principales

### 1. **MainActivity** (`MainActivity.kt`)
- Pantalla principal de canales
- Gestión de UI y controles remoto
- Navegación entre canales
- Soporte para D-Pad y controles

**Responsabilidades:**
- Cargar lista de canales
- Mostrar grid de canales
- Abrir reproductor al seleccionar canal
- Manejo de eventos de control remoto

### 2. **VideoPlayerActivity** (`VideoPlayerActivity.kt`)
- Reproductor nativo con ExoPlayer
- Aislado completamente (sin WebView)
- Controles de reproducción
- Soporte HLS y MP4

**Responsabilidades:**
- Reproducir stream de video
- Mostrar controles del player
- Manejar eventos de teclado
- Auto-ocultar controles

### 3. **ChannelRepository** (`data/ChannelRepository.kt`)
- Obtiene canales de API remota
- Cachea datos localmente
- Fallback a canales locales
- Parseo de JSON

**Responsabilidades:**
- Conectar a API
- Deserializar JSON
- Manejar errores de red
- Filtrado y búsqueda

### 4. **RemoteController** (`data/RemoteController.kt`)
- Lógica de navegación remota
- Control de volumen
- Selección de canales

**Responsabilidades:**
- Rastrear índice seleccionado
- Validar movimientos
- Calcular volumen

### 5. **ChannelsAdapter** (`ui/ChannelsAdapter.kt`)
- RecyclerView Adapter
- Carga de logos con Picasso
- ViewHolder de canal

**Responsabilidades:**
- Renderizar items de canal
- Cargar imágenes
- Manejar clicks

## Flujo de Datos

```
┌──────────────┐
│   User Input │
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│  MainActivity    │
│ (Event Handler)  │
└──────┬───────────┘
       │
       ├─► ChannelRepository.loadChannels()
       │           │
       │           ▼
       │   ┌──────────────┐
       │   │  OkHttp      │
       │   │  Network     │
       │   └──────┬───────┘
       │          │
       │          ▼
       │   ┌──────────────┐
       │   │  API/Local   │
       │   │  Fallback    │
       │   └──────┬───────┘
       │          │
       ├─ Channel Click ─────┐
       │          │           │
       ▼          ▼           ▼
  ChannelsAdapter  VideoPlayerActivity
  (Render)         │
                   ├─► ExoPlayer.load(url)
                   │        │
                   │        ▼
                   │   ┌──────────────┐
                   │   │  HLS/MP4     │
                   │   │  Stream      │
                   │   └──────────────┘
                   │
                   ▼
              Video Playing
```

## Patrones de Diseño Utilizados

### 1. **MVVM (Model-View-ViewModel)**
- Separación de responsabilidades
- DataBinding con LiveData
- ViewModels para persistencia

### 2. **Repository Pattern**
- Abstracción de fuente de datos
- Facilita testing
- Implementación limpia

### 3. **Adapter Pattern**
- RecyclerView Adapter
- Transformación de datos

### 4. **Singleton Pattern**
- ChannelRepository
- AppConfig

### 5. **Listener Pattern**
- Player event listeners
- UI event handlers

## Gestión de Dependencias

### Gradle Dependency Management
```gradle
dependencies {
    // Jetpack
    androidx.appcompat:appcompat
    androidx.lifecycle:lifecycle-*
    
    // ExoPlayer
    com.google.android.exoplayer2:exoplayer
    com.google.android.exoplayer2:extension-hls
    
    // Networking
    com.squareup.okhttp3:okhttp
    com.google.code.gson:gson
    
    // Image Loading
    com.squareup.picasso:picasso
    
    // Async
    org.jetbrains.kotlinx:kotlinx-coroutines-*
}
```

## Flujo de Compilación

```
Source Code (.kt)
    │
    ▼
Kotlin Compiler
    │
    ▼
Bytecode (.class)
    │
    ▼
D8/Dex Compiler
    │
    ▼
DEX Files
    │
    ▼
Android Resources + Manifest
    │
    ▼
APK Packager
    │
    ▼
APK Signed (Release)
    │
    ▼
APK Optimized (zipalign)
    │
    ▼
Final APK ✓
```

## Seguridad

### 1. **Aislamiento de Network**
```kotlin
val httpDataSourceFactory = DefaultHttpDataSource.Factory()
    .setAllowCrossProtocolRedirects(false) // Previene redirecciones
    .setUserAgent("BautiTV/1.0")
```

### 2. **Sin WebView**
- 100% nativo
- Sin ejecución de JS
- Mejor seguridad

### 3. **Validación de URLs**
- Solo streams permitidos
- Sin navegación a navegadores

## Escalabilidad

### Para agregar nuevas características:

1. **Agregar repositorio de datos**
   ```
   data/NewRepository.kt
   ```

2. **Crear Activity/Fragment**
   ```
   activities/NewActivity.kt
   ```

3. **Agregar layouts**
   ```
   res/layout/activity_new.xml
   ```

4. **Agregar dependencia en build.gradle**

5. **Registrar en AndroidManifest.xml**

## Performance

### Optimizaciones implementadas:

1. **Corrutinas**: Operaciones en background
2. **ViewBinding**: Evita findViewById()
3. **RecyclerView**: Recicla vistas
4. **ExoPlayer**: Buffer inteligente
5. **Picasso**: Cache de imágenes
6. **ProGuard**: Minificación en release

## Testing

### Estructura para testing:
```
app/src/test/          # Unit tests
app/src/androidTest/   # Instrumented tests
```

### Ejemplo de test:
```kotlin
@Test
fun testChannelLoading() {
    val repo = ChannelRepository
    val channels = runBlocking {
        repo.loadChannels(context)
    }
    assertNotNull(channels)
}
```

## Configuración Actual

| Aspecto | Valor |
|--------|-------|
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 (Android 14) |
| Language | Kotlin 1.9+ |
| BuildTools | 34.0.0+ |
| Format | Native (No WebView) |

## Próximas Mejoras Posibles

- [ ] Caché persistente de canales
- [ ] Historial de reproducción
- [ ] Favoritos/Watchlist
- [ ] Subtítulos y múltiples audio
- [ ] Descarga offline
- [ ] Analytics
- [ ] Suscripciones

## Recursos Útiles

- [Android Architecture Guide](https://developer.android.com/architecture)
- [ExoPlayer Documentation](https://exoplayer.dev)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Android Jetpack](https://developer.android.com/jetpack)

---

**Última actualización**: May 17, 2024
