#!/bin/bash
# BautiTV Complete Fix Script for Termux
# Navigate to Bauti67 directory and run this script

set -e  # Exit on error

echo "🚀 Starting BautiTV Fix..."
echo ""

# Verify we're in the right directory
if [ ! -f "app/build.gradle" ]; then
    echo "❌ Error: Not in Bauti67 directory"
    echo "Run: cd ~/Bauti67 && bash fix_bauti.sh"
    exit 1
fi

echo "✏️  Updating build.gradle..."
cat > app/build.gradle << 'GRADLE_EOF'
plugins {
    id 'com.android.application'
    id 'kotlin-android'
}

android {
    namespace "com.bautiapp.tv"
    compileSdk 34

    defaultConfig {
        applicationId "com.bautiapp.tv"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = '11'
    }
}

dependencies {
    implementation 'androidx.core:core:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
    implementation 'com.squareup.okhttp3:okhttp:4.11.0'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.squareup.picasso:picasso:2.8'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
}
GRADLE_EOF
echo "✅ build.gradle updated"

echo "📁 Creating directories..."
mkdir -p app/src/main/java/com/bautiapp/tv/data
mkdir -p app/src/main/java/com/bautiapp/tv/ui
mkdir -p app/src/main/res/layout

echo "📝 Creating Channel.kt..."
cat > app/src/main/java/com/bautiapp/tv/data/Channel.kt << 'KT_EOF'
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
KT_EOF
echo "✅ Channel.kt created"

echo "📝 Creating TMDBService.kt..."
cat > app/src/main/java/com/bautiapp/tv/data/TMDBService.kt << 'KT_EOF'
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
KT_EOF
echo "✅ TMDBService.kt created"

echo "📝 Creating ChannelRepository.kt..."
cat > app/src/main/java/com/bautiapp/tv/data/ChannelRepository.kt << 'KT_EOF'
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
KT_EOF
echo "✅ ChannelRepository.kt created"

echo "📝 Creating ChannelsAdapter.kt..."
cat > app/src/main/java/com/bautiapp/tv/ui/ChannelsAdapter.kt << 'KT_EOF'
package com.bautiapp.tv.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.bautiapp.tv.R
import com.bautiapp.tv.data.Channel
import com.squareup.picasso.Picasso

class ChannelsAdapter(
    private val onChannelClick: (Channel) -> Unit
) : ListAdapter<Channel, ChannelsAdapter.ChannelViewHolder>(ChannelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(getItem(position), onChannelClick)
    }

    class ChannelViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val channelLogo: ImageView = itemView.findViewById(R.id.channel_logo)
        private val channelName: TextView = itemView.findViewById(R.id.channel_name)
        private val channelGroup: TextView? = itemView.findViewById(R.id.channel_group)

        fun bind(channel: Channel, onChannelClick: (Channel) -> Unit) {
            channelName.text = channel.name
            channelGroup?.text = channel.group
            
            val imageUrl = channel.poster ?: channel.logo
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).into(channelLogo)
            } else {
                channelLogo.setImageResource(android.R.drawable.ic_media_play)
            }
            
            itemView.setOnClickListener { onChannelClick(channel) }
        }
    }

    class ChannelDiffCallback : DiffUtil.ItemCallback<Channel>() {
        override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem.id == newItem.id && oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem == newItem
        }
    }
}
KT_EOF
echo "✅ ChannelsAdapter.kt created"

echo "📝 Creating WebViewPlayerActivity.kt..."
cat > app/src/main/java/com/bautiapp/tv/WebViewPlayerActivity.kt << 'KT_EOF'
package com.bautiapp.tv

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class WebViewPlayerActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_player)

        val webViewContainer: FrameLayout = findViewById(R.id.webview_container)
        webView = WebView(this)
        
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        webSettings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        
        webView.webViewClient = WebViewClient()
        
        val channelUrl = intent.getStringExtra("url") ?: ""
        val channelName = intent.getStringExtra("name") ?: "Player"
        val type = intent.getStringExtra("type") ?: "channel"
        
        title = channelName
        
        val html = buildPlayerHtml(channelUrl, channelName, type)
        webView.loadDataWithBaseURL("https://vidfast.pro/", html, "text/html", "utf-8", null)
        
        webViewContainer.addView(webView)
    }

    private fun buildPlayerHtml(url: String, name: String, type: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>$name</title>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/hls.js/1.4.12/hls.min.js"></script>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { background: #000; width: 100vw; height: 100vh; display: flex; align-items: center; justify-content: center; }
                    video { width: 100%; height: 100%; max-width: 100%; }
                    .loader { color: #e50914; text-align: center; }
                </style>
            </head>
            <body>
                <div class="loader"><p>Cargando: $name</p></div>
                <video id="player" controls></video>
                <script>
                    var url = "$url";
                    var video = document.getElementById("player");
                    
                    if(url.startsWith('tmdb_')) {
                        var tmdbId = url.split('_')[2];
                        var searchUrl = url.includes('movie') ? 
                            'https://vidfast.pro/search?query=' + tmdbId :
                            'https://vidfast.pro/search?query=' + tmdbId;
                        window.location.href = searchUrl;
                    } else if(/\.m3u8?(\?|$)/i.test(url) || url.includes('m3u')) {
                        if(typeof Hls !== 'undefined' && Hls.isSupported()) {
                            var hls = new Hls();
                            hls.loadSource(url);
                            hls.attachMedia(video);
                            hls.on(Hls.Events.MANIFEST_PARSED, function() {
                                video.play().catch(function(){});
                            });
                        }
                    } else {
                        video.src = url;
                        video.play().catch(function(){});
                    }
                </script>
            </body>
            </html>
        """
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::webView.isInitialized) {
            webView.destroy()
        }
    }
}
KT_EOF
echo "✅ WebViewPlayerActivity.kt created"

echo "📝 Creating MainActivity.kt..."
cat > app/src/main/java/com/bautiapp/tv/MainActivity.kt << 'KT_EOF'
package com.bautiapp.tv

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.bautiapp.tv.data.Channel
import com.bautiapp.tv.data.ChannelRepository
import com.bautiapp.tv.ui.ChannelsAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var channelsAdapter: ChannelsAdapter
    private var allChannels: List<Channel> = emptyList()
    private var currentSearchType = "channels"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val channelCount: TextView = findViewById(R.id.channel_count)
        val searchInput: EditText = findViewById(R.id.search_input)
        val searchBtn: ImageButton = findViewById(R.id.search_btn)
        val channelsGrid: RecyclerView = findViewById(R.id.channels_grid)
        val moviesBtn: ImageButton? = findViewById(R.id.btn_movies)
        val seriesBtn: ImageButton? = findViewById(R.id.btn_series)
        val channelsBtn: ImageButton? = findViewById(R.id.btn_channels)
        val remoteToggle: ImageButton = findViewById(R.id.btn_remote_toggle)

        channelsGrid.layoutManager = GridLayoutManager(this, 3)
        channelsAdapter = ChannelsAdapter { channel -> playContent(channel) }
        channelsGrid.adapter = channelsAdapter

        loadChannels()

        searchBtn.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isEmpty()) {
                Toast.makeText(this, "Ingresa un término de búsqueda", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            search(query)
        }

        searchInput.setOnEditorActionListener { _, _, _ ->
            val query = searchInput.text.toString().trim()
            if (query.isNotEmpty()) search(query)
            true
        }

        moviesBtn?.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isEmpty()) {
                Toast.makeText(this, "Ingresa un término de búsqueda", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentSearchType = "movies"
            searchMovies(query)
        }

        seriesBtn?.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isEmpty()) {
                Toast.makeText(this, "Ingresa un término de búsqueda", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentSearchType = "series"
            searchSeries(query)
        }

        channelsBtn?.setOnClickListener {
            currentSearchType = "channels"
            loadChannels()
        }

        remoteToggle.setOnClickListener {
            startActivity(Intent(this, VideoPlayerActivity::class.java))
        }
    }

    private fun loadChannels() {
        lifecycleScope.launch {
            try {
                allChannels = ChannelRepository.getChannels()
                channelsAdapter.submitList(allChannels)
                updateChannelCount(allChannels.size)
            } catch (e: Exception) {
                updateChannelCount(0)
            }
        }
    }

    private fun search(query: String) {
        when(currentSearchType) {
            "movies" -> searchMovies(query)
            "series" -> searchSeries(query)
            else -> searchChannels(query)
        }
    }

    private fun searchChannels(query: String) {
        lifecycleScope.launch {
            try {
                val filtered = ChannelRepository.searchChannels(query, allChannels)
                channelsAdapter.submitList(filtered)
                updateChannelCount(filtered.size)
            } catch (e: Exception) {
                updateChannelCount(0)
            }
        }
    }

    private fun searchMovies(query: String) {
        lifecycleScope.launch {
            try {
                val movies = ChannelRepository.searchMovies(query)
                channelsAdapter.submitList(movies)
                updateChannelCount(movies.size)
            } catch (e: Exception) {
                updateChannelCount(0)
            }
        }
    }

    private fun searchSeries(query: String) {
        lifecycleScope.launch {
            try {
                val series = ChannelRepository.searchSeries(query)
                channelsAdapter.submitList(series)
                updateChannelCount(series.size)
            } catch (e: Exception) {
                updateChannelCount(0)
            }
        }
    }

    private fun updateChannelCount(count: Int) {
        val countView: TextView = findViewById(R.id.channel_count)
        countView.text = "$count resultados"
    }

    private fun playContent(channel: Channel) {
        val intent = Intent(this, WebViewPlayerActivity::class.java)
        intent.putExtra("url", channel.url)
        intent.putExtra("name", channel.name)
        intent.putExtra("type", channel.type)
        startActivity(intent)
    }
}
KT_EOF
echo "✅ MainActivity.kt created"

echo "📝 Creating WebView layout..."
cat > app/src/main/res/layout/activity_webview_player.xml << 'XML_EOF'
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/webview_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/black" />
XML_EOF
echo "✅ WebView layout created"

echo "📝 Updating AndroidManifest.xml..."
cat > app/src/main/AndroidManifest.xml << 'XML_EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.AppCompat.NoActionBar">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".WebViewPlayerActivity"
            android:configChanges="orientation|screenSize"
            android:exported="false" />

        <activity
            android:name=".VideoPlayerActivity"
            android:exported="false" />

    </application>

</manifest>
XML_EOF
echo "✅ AndroidManifest.xml updated"

echo ""
echo "🏗️  Building the app..."
echo ""

./gradlew clean build

if [ $? -eq 0 ]; then
    echo ""
    echo "🔨 Building APK..."
    ./gradlew assembleDebug
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ BUILD SUCCESS!"
        echo ""
        echo "📱 APK Location:"
        echo "   app/build/outputs/apk/debug/app-debug.apk"
        echo ""
        echo "To install:"
        echo "   adb install -r app/build/outputs/apk/debug/app-debug.apk"
        echo ""
    else
        echo "❌ APK build failed"
        exit 1
    fi
else
    echo "❌ Build failed"
    exit 1
fi
