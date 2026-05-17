package com.bautiapp.tv.data

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request

object TMDBService {
    private val client = OkHttpClient()
    private const val TMDB_API_KEY = "4ef0d1b53b77ffb5edc3f0f4873eb28d"
    private const val BASE_URL = "https://api.themoviedb.org/3"
    private const val IMAGE_BASE = "https://image.tmdb.org/t/p/w500"

    fun searchMovies(query: String): List<Channel> {
        return try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = "$BASE_URL/search/movie?api_key=$TMDB_API_KEY&query=$encodedQuery&language=es-ES"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return emptyList()
            
            val jsonBody = response.body?.string() ?: return emptyList()
            val gson = com.google.gson.Gson()
            val jsonObject = gson.fromJson(jsonBody, JsonObject::class.java)
            val results = jsonObject.getAsJsonArray("results") ?: return emptyList()
            
            results.mapNotNull { item ->
                val obj = item.asJsonObject
                val id = obj.get("id")?.asInt ?: return@mapNotNull null
                val title = obj.get("title")?.asString ?: "Unknown"
                val poster = obj.get("poster_path")?.asString
                val overview = obj.get("overview")?.asString
                val rating = obj.get("vote_average")?.asFloat ?: 0f
                
                Channel(
                    id = id,
                    name = title,
                    url = "tmdb_movie_$id",
                    poster = if (poster != null) IMAGE_BASE + poster else null,
                    description = overview,
                    rating = rating,
                    group = "Movies",
                    type = "movie"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun searchSeries(query: String): List<Channel> {
        return try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = "$BASE_URL/search/tv?api_key=$TMDB_API_KEY&query=$encodedQuery&language=es-ES"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return emptyList()
            
            val jsonBody = response.body?.string() ?: return emptyList()
            val gson = com.google.gson.Gson()
            val jsonObject = gson.fromJson(jsonBody, JsonObject::class.java)
            val results = jsonObject.getAsJsonArray("results") ?: return emptyList()
            
            results.mapNotNull { item ->
                val obj = item.asJsonObject
                val id = obj.get("id")?.asInt ?: return@mapNotNull null
                val title = obj.get("name")?.asString ?: "Unknown"
                val poster = obj.get("poster_path")?.asString
                val overview = obj.get("overview")?.asString
                val rating = obj.get("vote_average")?.asFloat ?: 0f
                
                Channel(
                    id = id,
                    name = title,
                    url = "tmdb_series_$id",
                    poster = if (poster != null) IMAGE_BASE + poster else null,
                    description = overview,
                    rating = rating,
                    group = "Series",
                    type = "series"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
