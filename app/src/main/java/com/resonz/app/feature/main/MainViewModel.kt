package com.resonz.app.feature.main

import androidx.lifecycle.ViewModel
import com.resonz.app.model.*
import com.resonz.app.session.SessionCoordinator
import com.resonz.app.session.SessionUiState
import com.resonz.app.session.SoundMode
import com.resonz.app.session.TransportState
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    val coordinator = SessionCoordinator()
    
    val uiState: StateFlow<SessionUiState> = coordinator.uiState

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.LoadInitialState -> { }
            is MainAction.SelectPreset -> coordinator.selectPreset(action.presetId)
            is MainAction.SetBeatNormalized -> coordinator.setBeat(action.value)
            is MainAction.SetDriftLevel -> coordinator.setDrift(action.level)
            is MainAction.SetTimePreset -> coordinator.setTime(action.preset)
            is MainAction.SetToneNormalized -> coordinator.setTone(action.value)
            is MainAction.ToggleWatchSync -> { }
            MainAction.PlayPause -> coordinator.startOrPause()
            MainAction.Stop -> coordinator.stop()
        }
    }

    fun beatNormalized(): Float = coordinator.beatNormalized()
    fun toneLabel(): String = coordinator.toneLabel()
    fun timeLabel(): String = coordinator.timeLabel()
    fun driftPosition(): Float = coordinator.driftPosition()
    fun timePosition(): Float = coordinator.timePosition()
    fun setDriftFromPosition(position: Float) = coordinator.setDriftFromPosition(position)
    fun setTimeFromPosition(position: Float) = coordinator.setTimeFromPosition(position)
    
    fun setSoundMode(mode: SoundMode) = coordinator.setSoundMode(mode)
    
    fun isPlaying(): Boolean = uiState.value.transportState == TransportState.PLAYING
    fun isPaused(): Boolean = uiState.value.transportState == TransportState.PAUSED
}
