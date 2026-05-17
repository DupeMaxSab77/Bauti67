package com.bautiapp.tv

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VideoPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val channelName = findViewById<TextView>(R.id.channel_name)
        val loadingIndicator = findViewById<ProgressBar>(R.id.loading_indicator)

        channelName.text = "Loading..."
        loadingIndicator.visibility = android.view.View.GONE
    }
}
