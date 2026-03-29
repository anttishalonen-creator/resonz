package com.resonz.app.feature.playback

import androidx.lifecycle.ViewModel
import com.resonz.app.session.SessionCoordinator
import com.resonz.app.session.SessionUiState
import kotlinx.coroutines.flow.StateFlow

class PlaybackViewModel(val coordinator: SessionCoordinator) : ViewModel() {
    val uiState: StateFlow<SessionUiState> = coordinator.uiState
    
    fun startOrPause() {
        coordinator.startOrPause()
    }
    
    fun stop() {
        coordinator.stop()
    }
}
