package com.resonz.app.feature.main

import androidx.lifecycle.ViewModel
import com.resonz.app.audio.RealTimeAudioEngine
import com.resonz.app.domain.*
import com.resonz.app.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val audioEngine = RealTimeAudioEngine()
    private val _uiState = MutableStateFlow(
        MainUiState(PresetDefaults.deepSleep(), SessionStatus.IDLE, WatchSyncStatus.OFF, 0, timePresetToSeconds(PresetDefaults.deepSleep().timePreset), 3.0, null)
    )
    val uiState: StateFlow<MainUiState> = _uiState

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.LoadInitialState -> pushAudioTargets(_uiState.value.config)
            is MainAction.SelectPreset -> selectPreset(action.presetId)
            is MainAction.SetBeatNormalized -> updateConfig { copy(beatHz = normalizedToBeatHz(action.value)) }
            is MainAction.SetDriftLevel -> updateConfig { copy(driftLevel = action.level) }
            is MainAction.SetTimePreset -> updateConfig { copy(timePreset = action.preset) }
            is MainAction.SetToneNormalized -> updateConfig { copy(toneNormalized = action.value) }
            is MainAction.ToggleWatchSync -> updateConfig { copy(watchSyncEnabled = action.enabled) }
            MainAction.PlayPause -> togglePlayback()
            MainAction.Stop -> stopPlayback()
        }
    }

    private fun selectPreset(id: PresetId) {
        val config = PresetDefaults.byId(id)
        _uiState.update {
            it.copy(
                config = config,
                currentTotalSec = timePresetToSeconds(config.timePreset),
                activeBeatDisplayHz = config.beatHz,
                statusMessage = null,
                watchSyncStatus = if (config.watchSyncEnabled) WatchSyncStatus.WAITING else WatchSyncStatus.OFF,
            )
        }
        pushAudioTargets(config)
    }

    private fun updateConfig(transform: SessionConfig.() -> SessionConfig) {
        val updated = _uiState.value.config.transform()
        val finalConfig = updated.copy(isModified = ConfigModificationChecker.isModified(updated))
        _uiState.update {
            it.copy(
                config = finalConfig,
                currentTotalSec = timePresetToSeconds(finalConfig.timePreset),
                activeBeatDisplayHz = finalConfig.beatHz,
                statusMessage = if (finalConfig.isModified) "Modified" else null,
                watchSyncStatus = when {
                    finalConfig.presetId == PresetId.FOCUS && finalConfig.watchSyncEnabled -> WatchSyncStatus.UNSUPPORTED_FOR_PRESET
                    finalConfig.watchSyncEnabled -> WatchSyncStatus.WAITING
                    else -> WatchSyncStatus.OFF
                },
            )
        }
        pushAudioTargets(finalConfig)
    }

    private fun togglePlayback() {
        when (_uiState.value.sessionStatus) {
            SessionStatus.PLAYING -> { audioEngine.pause(); _uiState.update { it.copy(sessionStatus = SessionStatus.PAUSED) } }
            SessionStatus.PAUSED -> { audioEngine.resume(); _uiState.update { it.copy(sessionStatus = SessionStatus.PLAYING) } }
            else -> {
                val config = _uiState.value.config
                audioEngine.prepare(config)
                audioEngine.start(config)
                _uiState.update { it.copy(sessionStatus = SessionStatus.PLAYING) }
            }
        }
    }

    private fun stopPlayback() {
        audioEngine.stop()
        _uiState.update { it.copy(sessionStatus = SessionStatus.IDLE) }
    }

    private fun pushAudioTargets(config: SessionConfig) {
        audioEngine.updateTargets(sessionConfigToAudioTargets(config))
    }

    fun beatNormalized(): Float = beatHzToNormalized(_uiState.value.config.beatHz)
    fun toneLabel(): String = toneNormalizedToLabel(_uiState.value.config.toneNormalized).name.lowercase().replaceFirstChar { it.uppercase() }
    fun timeLabel(): String = timePresetToLabel(_uiState.value.config.timePreset)
    fun driftPosition(): Float = when (_uiState.value.config.driftLevel) {
        DriftLevel.OFF -> 0f; DriftLevel.LOW -> 1f / 3f; DriftLevel.MEDIUM -> 2f / 3f; DriftLevel.HIGH -> 1f
    }
    fun timePosition(): Float = when (_uiState.value.config.timePreset) {
        TimePreset.MIN_20 -> 0f; TimePreset.MIN_30 -> 0.25f; TimePreset.MIN_45 -> 0.5f; TimePreset.MIN_90 -> 0.75f; TimePreset.ALL_NIGHT -> 1f
    }
    fun setDriftFromPosition(position: Float) = onAction(MainAction.SetDriftLevel(when {
        position < 0.17f -> DriftLevel.OFF
        position < 0.50f -> DriftLevel.LOW
        position < 0.84f -> DriftLevel.MEDIUM
        else -> DriftLevel.HIGH
    }))
    fun setTimeFromPosition(position: Float) = onAction(MainAction.SetTimePreset(when {
        position < 0.125f -> TimePreset.MIN_20
        position < 0.375f -> TimePreset.MIN_30
        position < 0.625f -> TimePreset.MIN_45
        position < 0.875f -> TimePreset.MIN_90
        else -> TimePreset.ALL_NIGHT
    }))
}
