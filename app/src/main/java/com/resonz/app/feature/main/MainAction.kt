package com.resonz.app.feature.main

import com.resonz.app.model.DriftLevel
import com.resonz.app.model.PresetId
import com.resonz.app.model.TimePreset

sealed interface MainAction {
    data class SelectPreset(val presetId: PresetId) : MainAction
    data class SetBeatNormalized(val value: Float) : MainAction
    data class SetDriftLevel(val level: DriftLevel) : MainAction
    data class SetTimePreset(val preset: TimePreset) : MainAction
    data class SetToneNormalized(val value: Float) : MainAction
    data class ToggleWatchSync(val enabled: Boolean) : MainAction
    data object PlayPause : MainAction
    data object Stop : MainAction
    data object LoadInitialState : MainAction
}
