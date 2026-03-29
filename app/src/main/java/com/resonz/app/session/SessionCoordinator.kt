package com.resonz.app.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resonz.app.audio.PresetAudioProfiles
import com.resonz.app.audio.RealTimeAudioEngine
import com.resonz.app.model.*
import com.resonz.app.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SessionCoordinator : ViewModel() {
    private val audioEngine = RealTimeAudioEngine()
    
    private val _uiState = MutableStateFlow(
        SessionUiState(
            config = PresetDefaults.deepSleep(),
            transportState = TransportState.IDLE,
            elapsedSec = 0,
            totalSec = timePresetToSeconds(PresetDefaults.deepSleep().timePreset),
            displayedBeatHz = PresetAudioProfiles.fromPreset(PresetId.DEEP_SLEEP).beatHz.toDouble(),
            soundMode = SoundMode.OCEAN,
            isModified = false
        )
    )
    val uiState: StateFlow<SessionUiState> = _uiState
    
    private var timerJob: Job? = null
    
    fun selectPreset(presetId: PresetId) {
        val config = PresetDefaults.byId(presetId)
        val profile = PresetAudioProfiles.fromPreset(presetId)
        
        _uiState.update {
            it.copy(
                config = config,
                totalSec = timePresetToSeconds(config.timePreset),
                displayedBeatHz = profile.beatHz.toDouble(),
                isModified = false,
                soundMode = SoundMode.OCEAN
            )
        }
    }
    
    fun setBeat(normalized: Float) {
        updateConfig { copy(beatHz = normalizedToBeatHz(normalized)) }
    }
    
    fun setTone(normalized: Float) {
        updateConfig { copy(toneNormalized = normalized) }
    }
    
    fun setDrift(level: DriftLevel) {
        updateConfig { copy(driftLevel = level) }
    }
    
    fun setTime(preset: TimePreset) {
        updateConfig { copy(timePreset = preset) }
    }
    
    fun setSoundMode(mode: SoundMode) {
        _uiState.update { it.copy(soundMode = mode) }
        audioEngine.setSoundMode(mode)
    }
    
    private fun updateConfig(transform: SessionConfig.() -> SessionConfig) {
        val current = _uiState.value.config
        val updated = current.transform()
        val isModified = ConfigModificationChecker.isModified(updated)
        
        _uiState.update {
            it.copy(
                config = updated.copy(isModified = isModified),
                totalSec = timePresetToSeconds(updated.timePreset)
            )
        }
    }
    
    fun startOrPause() {
        when (_uiState.value.transportState) {
            TransportState.IDLE, TransportState.STOPPED -> start()
            TransportState.PLAYING -> pause()
            TransportState.PAUSED -> resume()
            else -> { }
        }
    }
    
    private fun start() {
        val config = _uiState.value.config
        
        audioEngine.prepare(config)
        audioEngine.start(config)
        
        _uiState.update {
            it.copy(
                transportState = TransportState.PLAYING,
                elapsedSec = 0
            )
        }
        
        startTimer()
    }
    
    private fun pause() {
        audioEngine.pause()
        timerJob?.cancel()
        _uiState.update { it.copy(transportState = TransportState.PAUSED) }
    }
    
    private fun resume() {
        audioEngine.resume()
        _uiState.update { it.copy(transportState = TransportState.PLAYING) }
        startTimer()
    }
    
    fun stop() {
        audioEngine.stop()
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                transportState = TransportState.IDLE,
                elapsedSec = 0
            )
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _uiState.value.transportState == TransportState.PLAYING) {
                delay(1000)
                _uiState.update { 
                    val newElapsed = it.elapsedSec + 1
                    if (newElapsed >= it.totalSec) {
                        stop()
                        it.copy(elapsedSec = it.totalSec)
                    } else {
                        it.copy(elapsedSec = newElapsed)
                    }
                }
            }
        }
    }
    
    fun beatNormalized(): Float = beatHzToNormalized(_uiState.value.config.beatHz)
    
    fun toneLabel(): String = toneNormalizedToLabel(_uiState.value.config.toneNormalized).name.lowercase().replaceFirstChar { it.uppercase() }
    
    fun timeLabel(): String = timePresetToLabel(_uiState.value.config.timePreset)
    
    fun driftPosition(): Float = when (_uiState.value.config.driftLevel) {
        DriftLevel.OFF -> 0f
        DriftLevel.LOW -> 1f / 3f
        DriftLevel.MEDIUM -> 2f / 3f
        DriftLevel.HIGH -> 1f
    }
    
    fun timePosition(): Float = when (_uiState.value.config.timePreset) {
        TimePreset.MIN_20 -> 0f
        TimePreset.MIN_30 -> 0.25f
        TimePreset.MIN_45 -> 0.5f
        TimePreset.MIN_90 -> 0.75f
        TimePreset.ALL_NIGHT -> 1f
    }
    
    fun setDriftFromPosition(position: Float) {
        val level = when {
            position < 0.17f -> DriftLevel.OFF
            position < 0.50f -> DriftLevel.LOW
            position < 0.84f -> DriftLevel.MEDIUM
            else -> DriftLevel.HIGH
        }
        setDrift(level)
    }
    
    fun setTimeFromPosition(position: Float) {
        val preset = when {
            position < 0.125f -> TimePreset.MIN_20
            position < 0.375f -> TimePreset.MIN_30
            position < 0.625f -> TimePreset.MIN_45
            position < 0.875f -> TimePreset.MIN_90
            else -> TimePreset.ALL_NIGHT
        }
        setTime(preset)
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        audioEngine.release()
    }
}
