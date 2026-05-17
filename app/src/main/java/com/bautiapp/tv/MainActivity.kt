package com.bautiapp.tv

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bautiapp.tv.databinding.ActivityMainBinding
import com.bautiapp.tv.data.ChannelRepository
import com.bautiapp.tv.data.RemoteController
import com.bautiapp.tv.ui.ChannelsAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var channelsAdapter: ChannelsAdapter
    private lateinit var remoteController: RemoteController
    private var selectedIndex = 0
    private var tvModeActive = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        remoteController = RemoteController()
        setupUI()
        loadChannels()
    }
    
    private fun setupUI() {
        // Setup channels grid
        channelsAdapter = ChannelsAdapter { channel ->
            playChannel(channel)
        }
        binding.channelsGrid.adapter = channelsAdapter
        
        // Setup TV toggle button
        binding.tvToggleBtn.setOnClickListener {
            toggleTVMode()
        }
        
        // Setup remote control buttons
        setupRemoteControls()
    }
    
    private fun setupRemoteControls() {
        binding.apply {
            btnUp.setOnClickListener { remoteController.navigateUp() }
            btnDown.setOnClickListener { remoteController.navigateDown() }
            btnLeft.setOnClickListener { remoteController.navigateLeft() }
            btnRight.setOnClickListener { remoteController.navigateRight() }
            btnOk.setOnClickListener { selectChannel() }
            btnBack.setOnClickListener { closeRemote() }
            btnVolUp.setOnClickListener { adjustVolume(1) }
            btnVolDown.setOnClickListener { adjustVolume(-1) }
        }
    }
    
    private fun loadChannels() {
        lifecycleScope.launch {
            try {
                val channels = ChannelRepository.loadChannels(this@MainActivity)
                channelsAdapter.submitList(channels)
                updateChannelCount(channels.size)
            } catch (e: Exception) {
                showError("Error loading channels: ${e.message}")
            }
        }
    }
    
    private fun playChannel(channel: Channel) {
        val intent = Intent(this, VideoPlayerActivity::class.java).apply {
            putExtra("channel_name", channel.name)
            putExtra("channel_url", channel.url)
            putExtra("channel_logo", channel.logo)
            putExtra("channel_group", channel.group)
        }
        startActivity(intent)
    }
    
    private fun selectChannel() {
        val channel = channelsAdapter.currentList.getOrNull(selectedIndex)
        if (channel != null) {
            playChannel(channel)
        }
    }
    
    private fun updateChannelCount(count: Int) {
        binding.channelCount.text = "$count canales"
    }
    
    private fun toggleTVMode() {
        tvModeActive = !tvModeActive
        binding.tvToggleBtn.isActivated = tvModeActive
        binding.remotePanel.visibility = if (tvModeActive) android.view.View.VISIBLE else android.view.View.GONE
    }
    
    private fun closeRemote() {
        binding.remotePanel.visibility = android.view.View.GONE
        tvModeActive = false
    }
    
    private fun adjustVolume(direction: Int) {
        val audioManager = getSystemService(android.media.AudioManager::class.java)
        audioManager?.adjustVolume(
            if (direction > 0) android.media.AudioManager.ADJUST_RAISE else android.media.AudioManager.ADJUST_LOWER,
            android.media.AudioManager.FLAG_SHOW_UI
        )
    }
    
    private fun showError(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
    
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                remoteController.navigateUp()
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                remoteController.navigateDown()
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                remoteController.navigateLeft()
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                remoteController.navigateRight()
                true
            }
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                selectChannel()
                true
            }
            KeyEvent.KEYCODE_BACK -> {
                if (tvModeActive) {
                    closeRemote()
                    true
                } else {
                    super.onKeyDown(keyCode, event)
                }
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}

data class Channel(
    val name: String,
    val url: String,
    val logo: String? = null,
    val group: String? = null,
    val source: String? = null
)
