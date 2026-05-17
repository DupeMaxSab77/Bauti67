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
