package com.bautiapp.tv.data

import com.google.gson.Gson

object ChannelRepository {
    private val gson = Gson()
    
    suspend fun getChannels(): List<Channel> {
        return try {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun searchChannels(query: String, channels: List<Channel>): List<Channel> {
        return channels.filter { 
            it.name.contains(query, ignoreCase = true)
        }
    }
}
