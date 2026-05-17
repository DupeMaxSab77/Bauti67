package com.bautiapp.tv.data

data class Channel(
    val name: String,
    val url: String,
    val logo: String? = null,
    val group: String = "General",
    val source: String? = null
)
