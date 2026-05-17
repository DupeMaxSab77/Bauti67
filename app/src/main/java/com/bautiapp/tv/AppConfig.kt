package com.bautiapp.tv

/**
 * Configuración global de la aplicación
 */
object AppConfig {
    
    // API Configuration
    const val API_BASE_URL = "https://bautiaiconfig.tiiny.site/"
    const val CHANNELS_ENDPOINT = "channels.json"
    const val IPTV_ENDPOINT = "api/iptv/channels"
    
    // Network Configuration
    const val CONNECT_TIMEOUT_SECONDS = 10L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
    
    // Player Configuration
    const val PLAYER_BUFFER_LENGTH = 20f
    const val CONTROLS_HIDE_TIMEOUT = 5000L // milliseconds
    const val SEEK_INCREMENT = 10000L // 10 seconds
    
    // UI Configuration
    const val CHANNELS_GRID_COLUMNS = 3
    const val MAX_CHANNELS_DISPLAY = 600
    
    // Cache Configuration
    const val CACHE_SIZE_MB = 100L
    const val CACHE_EXPIRE_HOURS = 24
    
    // Security
    const val ALLOW_CLEARTEXT_HTTP = true // Para streams de prueba
    const val BLOCK_EXTERNAL_REDIRECTS = true
    
    // Logging
    const val ENABLE_DEBUG_LOGS = true
    
    // Feature Flags
    const val ENABLE_TV_MODE = true
    const val ENABLE_REMOTE_CONTROL = true
    const val ENABLE_SEARCH = true
    const val ENABLE_CATEGORIES = true
}
