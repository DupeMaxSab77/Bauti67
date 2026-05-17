package com.bautiapp.tv

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var channelsAdapter: Any

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val channelCount: TextView = findViewById(R.id.channel_count)
        val searchInput: EditText = findViewById(R.id.search_input)
        val searchBtn: ImageButton = findViewById(R.id.search_btn)
        val channelsGrid: RecyclerView = findViewById(R.id.channels_grid)
        val remoteToggle: ImageButton = findViewById(R.id.btn_remote_toggle)

        channelsGrid.layoutManager = GridLayoutManager(this, 3)
        channelCount.text = "0 canales"

        searchBtn.setOnClickListener {
            val query = searchInput.text.toString()
            channelCount.text = "0 canales"
        }

        remoteToggle.setOnClickListener {
            // Remote control
        }
    }
}
