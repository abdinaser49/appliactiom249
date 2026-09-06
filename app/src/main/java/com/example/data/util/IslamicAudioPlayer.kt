package com.example.data.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val title: String = "",
    val subtitle: String = "",
    val currentUrl: String = "",
    val speed: Float = 1.0f,
    val isRepeat: Boolean = false,
    val currentSurahNumber: Int = 0
)

class IslamicAudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private val _state = MutableStateFlow(AudioPlayerState())
    val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    private val progressUpdater = object : Runnable {
        override fun run() {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    _state.value = _state.value.copy(
                        currentPositionMs = mp.currentPosition,
                        durationMs = mp.duration
                    )
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    fun playStream(url: String, title: String, subtitle: String = "", surahNumber: Int = 0) {
        try {
            if (_state.value.currentUrl == url && mediaPlayer != null) {
                if (!_state.value.isPlaying) {
                    resume()
                }
                return
            }

            release()

            _state.value = _state.value.copy(
                isBuffering = true,
                isPlaying = false,
                currentUrl = url,
                title = title,
                subtitle = subtitle,
                currentSurahNumber = surahNumber
            )

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                isLooping = _state.value.isRepeat
                setOnPreparedListener { mp ->
                    _state.value = _state.value.copy(
                        isBuffering = false,
                        isPlaying = true,
                        durationMs = mp.duration,
                        currentPositionMs = 0
                    )
                    mp.start()
                    setSpeed(_state.value.speed)
                    handler.post(progressUpdater)
                }
                setOnCompletionListener {
                    if (!_state.value.isRepeat) {
                        _state.value = _state.value.copy(
                            isPlaying = false,
                            currentPositionMs = _state.value.durationMs
                        )
                    }
                }
                setOnErrorListener { _, _, _ ->
                    _state.value = _state.value.copy(
                        isBuffering = false,
                        isPlaying = false
                    )
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(isBuffering = false, isPlaying = false)
        }
    }

    fun toggleRepeat() {
        val newRepeat = !_state.value.isRepeat
        _state.value = _state.value.copy(isRepeat = newRepeat)
        mediaPlayer?.isLooping = newRepeat
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _state.value = _state.value.copy(isPlaying = false)
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            it.start()
            _state.value = _state.value.copy(isPlaying = true)
            handler.post(progressUpdater)
        }
    }

    fun togglePlayPause() {
        if (_state.value.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _state.value = _state.value.copy(currentPositionMs = positionMs)
    }

    fun skip(seconds: Int) {
        val current = _state.value.currentPositionMs
        val target = (current + seconds * 1000).coerceIn(0, _state.value.durationMs)
        seekTo(target)
    }

    fun setSpeed(speed: Float) {
        try {
            _state.value = _state.value.copy(speed = speed)
            mediaPlayer?.let { mp ->
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    mp.playbackParams = mp.playbackParams.setSpeed(speed)
                }
            }
        } catch (e: Exception) {
            // Ignored if speed not supported on device
        }
    }

    fun release() {
        handler.removeCallbacks(progressUpdater)
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _state.value = AudioPlayerState()
    }
}
