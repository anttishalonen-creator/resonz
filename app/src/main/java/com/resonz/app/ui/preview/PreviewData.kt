package com.resonz.app.ui.preview

import com.resonz.app.model.MainUiState
import com.resonz.app.model.PresetDefaults
import com.resonz.app.model.SessionStatus
import com.resonz.app.model.WatchSyncStatus
import com.resonz.app.domain.timePresetToSeconds

object PreviewData {
    fun deepSleep() = MainUiState(
        config = PresetDefaults.deepSleep(),
        sessionStatus = SessionStatus.IDLE,
        watchSyncStatus = WatchSyncStatus.OFF,
        currentElapsedSec = 0,
        currentTotalSec = timePresetToSeconds(PresetDefaults.deepSleep().timePreset),
        activeBeatDisplayHz = 3.0,
        statusMessage = null,
    )
}
