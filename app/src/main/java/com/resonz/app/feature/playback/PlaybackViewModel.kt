package com.resonz.app.feature.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resonz.app.audio.EngineState
import com.resonz.app.audio.RealTimeAudioEngine
import com.resonz.app.model.SessionConfig
import com.resonz.app.model.SessionStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlaybackUiState(
    val presetName: String = "Deep Sleep",
    val beatHz: Double = 3.0,
    val elapsedSec: Int = 0,
    val totalSec: Int = 5400,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
)

class PlaybackViewModel : ViewModel() {
    private var audioEngine: RealTimeAudioEngine? = null
    private var timerJob: Job? = null
    
    private val _uiState = MutableStateFlow(PlaybackUiState())
    val uiState: StateFlow<PlaybackUiState> = _uiState

    fun setAudioEngine(engine: RealTimeAudioEngine) {
        audioEngine = engine
    }

    fun startSession(config: SessionConfig) {
        _uiState.update {
            it.copy(
                presetName = config.presetId.name.replace("_", " "),
                beatHz = config.beatHz,
                elapsedSec = 0,
                totalSec = when (config.timePreset) {
                    com.resonz.app.model.TimePreset.MIN_20 -> 1200
                    com.resonz.app.model.TimePreset.MIN_30 -> 1800
                    com.resonz.app.model.TimePreset.MIN_45 -> 2700
                    com.resonz.app.model.TimePreset.MIN_90 -> 5400
                    com.resonz.app.model.TimePreset.ALL_NIGHT -> 28800
                },
                isPlaying = true,
                isPaused = false,
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isPlaying && !_uiState.value.isPaused) {
                delay(1000)
                _uiState.update { it.copy(elapsedSec = it.elapsedSec + 1) }
            }
        }
    }

    fun togglePause() {
        val engine = audioEngine ?: return
        if (_uiState.value.isPaused) {
            engine.resume()
            _uiState.update { it.copy(isPaused = false) }
            startTimer()
        } else {
            engine.pause()
            _uiState.update { it.copy(isPaused = true) }
            timerJob?.cancel()
        }
    }

    fun stop() {
        audioEngine?.stop()
        timerJob?.cancel()
        _uiState.update { it.copy(isPlaying = false, isPaused = false) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
