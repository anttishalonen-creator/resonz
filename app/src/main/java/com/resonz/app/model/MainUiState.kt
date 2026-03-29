package com.resonz.app.model

data class MainUiState(
    val config: SessionConfig,
    val sessionStatus: SessionStatus,
    val watchSyncStatus: WatchSyncStatus,
    val currentElapsedSec: Int,
    val currentTotalSec: Int,
    val activeBeatDisplayHz: Double,
    val statusMessage: String?,
)
