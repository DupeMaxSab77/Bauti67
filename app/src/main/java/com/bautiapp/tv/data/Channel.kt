package com.bautiapp.tv.data

data class Channel(
    val id: Int = 0,
    val name: String,
    val url: String,
    val logo: String? = null,
    val poster: String? = null,
    val group: String = "General",
    val source: String? = null,
    val description: String? = null,
    val rating: Float = 0f,
    val type: String = "channel"
)
