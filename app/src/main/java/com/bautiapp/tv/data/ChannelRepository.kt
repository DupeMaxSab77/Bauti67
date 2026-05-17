package com.bautiapp.tv.data

import android.content.Context
import com.bautiapp.tv.Channel
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object ChannelRepository {
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    
    // Local test channels if API fails
    private val LOCAL_CHANNELS = listOf(
        Channel(
            name = "BBC News",
            url = "https://streams.example.com/bbc.m3u8",
            logo = null,
            group = "News"
        ),
        Channel(
            name = "Sky Sports",
            url = "https://streams.example.com/skysports.m3u8",
            logo = null,
            group = "Sports"
        ),
        Channel(
            name = "Movie Channel",
            url = "https://streams.example.com/movies.m3u8",
            logo = null,
            group = "Movies"
        )
    )
    
    suspend fun loadChannels(context: Context): List<Channel> = withContext(Dispatchers.IO) {
        try {
            // Try to load from API first
            val url = "https://bautiaiconfig.tiiny.site/channels.json"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "BautiTV/1.0")
                .build()
            
            val response = httpClient.newCall(request).execute()
            
            if (response.isSuccessful) {
                val body = response.body?.string() ?: return@withContext LOCAL_CHANNELS
                val channelsData = gson.fromJson(body, ChannelsResponse::class.java)
                
                return@withContext channelsData.toChannels()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Return local channels if API fails
        LOCAL_CHANNELS
    }
    
    suspend fun searchChannels(query: String, channels: List<Channel>): List<Channel> {
        return withContext(Dispatchers.Default) {
            channels.filter { 
                it.name.contains(query, ignoreCase = true) ||
                it.group?.contains(query, ignoreCase = true) == true
            }
        }
    }
}

data class ChannelsResponse(
    @SerializedName("groups")
    val groups: Map<String, List<ChannelData>>? = null,
    @SerializedName("total")
    val total: Int = 0
) {
    fun toChannels(): List<Channel> {
        val result = mutableListOf<Channel>()
        groups?.forEach { (group, channels) ->
            channels.forEach { ch ->
                result.add(
                    Channel(
                        name = ch.name ?: "Unknown",
                        url = ch.url ?: "",
                        logo = ch.logo,
                        group = group,
                        source = ch.source
                    )
                )
            }
        }
        return result
    }
}

data class ChannelData(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("logo")
    val logo: String? = null,
    @SerializedName("group")
    val group: String? = null,
    @SerializedName("source")
    val source: String? = null
)
