package com.bautiapp.tv

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bautiapp.tv.databinding.ActivityVideoPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class VideoPlayerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVideoPlayerBinding
    private var exoPlayer: ExoPlayer? = null
    private var channelName: String = ""
    private var channelUrl: String = ""
    private var channelLogo: String? = null
    private var controlsVisible = true
    private val controlsHideRunnable = Runnable { hideControls() }
    private val scope = MainScope()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Get channel data from intent
        channelName = intent.getStringExtra("channel_name") ?: "Unknown Channel"
        channelUrl = intent.getStringExtra("channel_url") ?: ""
        channelLogo = intent.getStringExtra("channel_logo")
        
        setupPlayer()
        setupUI()
    }
    
    private fun setupPlayer() {
        // Create ExoPlayer with custom HTTP data source to prevent external links
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(false)
            .setUserAgent("BautiTV/1.0")
        
        val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
            this,
            httpDataSourceFactory
        )
        
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
        
        exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                addListener(playerListener)
                binding.playerView.player = this
            }
        
        // Load and play channel
        loadChannel()
    }
    
    private fun loadChannel() {
        if (channelUrl.isEmpty()) {
            showError("URL del canal no disponible")
            return
        }
        
        try {
            val mediaItem = MediaItem.fromUri(channelUrl)
            exoPlayer?.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
            }
        } catch (e: Exception) {
            showError("Error al cargar el canal: ${e.message}")
        }
    }
    
    private fun setupUI() {
        // Channel name display
        binding.channelName.text = channelName
        
        // Controls click listeners
        binding.playerView.setOnClickListener {
            toggleControls()
        }
        
        binding.btnClose.setOnClickListener {
            finish()
        }
        
        binding.btnPlayPause.setOnClickListener {
            exoPlayer?.let {
                if (it.isPlaying) it.pause() else it.play()
            }
        }
        
        binding.btnFullscreen.setOnClickListener {
            toggleFullscreen()
        }
        
        // Hide controls after 5 seconds of inactivity
        scheduleControlsHide()
    }
    
    private fun toggleControls() {
        controlsVisible = !controlsVisible
        if (controlsVisible) {
            showControls()
        } else {
            hideControls()
        }
        scheduleControlsHide()
    }
    
    private fun showControls() {
        binding.controlsContainer.visibility = View.VISIBLE
        binding.topBar.visibility = View.VISIBLE
    }
    
    private fun hideControls() {
        binding.controlsContainer.visibility = View.GONE
        binding.topBar.visibility = View.GONE
        controlsVisible = false
    }
    
    private fun scheduleControlsHide() {
        binding.playerView.removeCallbacks(controlsHideRunnable)
        if (controlsVisible) {
            binding.playerView.postDelayed(controlsHideRunnable, 5000)
        }
    }
    
    private fun toggleFullscreen() {
        if (binding.playerView.isFullscreenButtonClicked) {
            binding.playerView.isFullscreenButtonClicked = false
        }
        // ExoPlayer handles fullscreen automatically with playerView
    }
    
    private fun showError(message: String) {
        binding.errorText.apply {
            text = message
            visibility = View.VISIBLE
        }
    }
    
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    binding.loadingIndicator.visibility = View.GONE
                }
                Player.STATE_BUFFERING -> {
                    binding.loadingIndicator.visibility = View.VISIBLE
                }
                Player.STATE_IDLE -> {}
                Player.STATE_ENDED -> {}
            }
        }
        
        override fun onPlayerError(error: PlaybackException) {
            showError("Error de reproducción: ${error.message}")
        }
    }
    
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_SPACE -> {
                exoPlayer?.let {
                    if (it.isPlaying) it.pause() else it.play()
                }
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                exoPlayer?.seekTo((exoPlayer?.currentPosition ?: 0) + 10000)
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                exoPlayer?.seekTo(maxOf(0, (exoPlayer?.currentPosition ?: 0) - 10000))
                true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                val audioManager = getSystemService(android.media.AudioManager::class.java)
                audioManager?.adjustVolume(android.media.AudioManager.ADJUST_RAISE, android.media.AudioManager.FLAG_SHOW_UI)
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val audioManager = getSystemService(android.media.AudioManager::class.java)
                audioManager?.adjustVolume(android.media.AudioManager.ADJUST_LOWER, android.media.AudioManager.FLAG_SHOW_UI)
                true
            }
            KeyEvent.KEYCODE_BACK -> {
                finish()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
    
    override fun onStart() {
        super.onStart()
        exoPlayer?.play()
    }
    
    override fun onStop() {
        super.onStop()
        exoPlayer?.pause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        binding.playerView.removeCallbacks(controlsHideRunnable)
        exoPlayer?.release()
        exoPlayer = null
    }
}
