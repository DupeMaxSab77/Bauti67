package com.bautiapp.tv.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChannelRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    private const val API_URL = "https://bautiaiconfig.tiiny.site/bautiai.json"
    
    suspend fun getChannels(): List<Channel> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = Request.Builder().url(API_URL).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()
            
            val jsonBody = response.body?.string() ?: return@withContext emptyList()
            val jsonObject = gson.fromJson(jsonBody, JsonObject::class.java)
            val channels = mutableListOf<Channel>()
            
            jsonObject.optJSONArray("channels")?.forEach { canal ->
                val obj = canal.asJsonObject
                channels.add(Channel(
                    id = obj.get("id")?.asInt ?: 0,
                    name = obj.get("nombre")?.asString ?: "Unknown",
                    url = obj.get("enlace")?.asString ?: "",
                    logo = obj.get("logo")?.asString,
                    group = obj.get("grupo")?.asString ?: "General",
                    source = obj.get("fuente")?.asString,
                    type = "channel"
                ))
            }
            
            channels
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun searchChannels(query: String, channels: List<Channel>): List<Channel> {
        if (query.isEmpty()) return channels
        val lowerQuery = query.lowercase()
        return channels.filter { channel ->
            channel.name.lowercase().contains(lowerQuery) ||
            channel.group.lowercase().contains(lowerQuery) ||
            (channel.source?.lowercase()?.contains(lowerQuery) ?: false)
        }
    }

    fun searchMovies(query: String): List<Channel> {
        return TMDBService.searchMovies(query)
    }

    fun searchSeries(query: String): List<Channel> {
        return TMDBService.searchSeries(query)
    }
}
