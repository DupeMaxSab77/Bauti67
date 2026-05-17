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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val channelCount: TextView = findViewById(R.id.channel_count)
        val searchInput: EditText = findViewById(R.id.search_input)
        val searchBtn: ImageButton = findViewById(R.id.search_btn)
        val channelsGrid: RecyclerView = findViewById(R.id.channels_grid)
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
            searchChannels(query)
        }

        searchInput.setOnEditorActionListener { _, _, _ ->
            val query = searchInput.text.toString().trim()
            if (query.isNotEmpty()) searchChannels(query)
            true
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
