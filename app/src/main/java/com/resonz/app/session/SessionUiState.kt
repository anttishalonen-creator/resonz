package com.resonz.app.session

import com.resonz.app.model.SessionConfig

enum class TransportState {
    IDLE, STARTING, PLAYING, PAUSING, PAUSED, STOPPING, STOPPED
}

enum class SoundMode {
    PURE, OCEAN
}

data class SessionUiState(
    val config: SessionConfig,
    val transportState: TransportState = TransportState.IDLE,
    val elapsedSec: Int = 0,
    val totalSec: Int = 0,
    val displayedBeatHz: Double = 3.0,
    val soundMode: SoundMode = SoundMode.OCEAN,
    val isModified: Boolean = false
)
