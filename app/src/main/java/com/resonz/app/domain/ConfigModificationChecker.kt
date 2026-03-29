package com.resonz.app.domain

import com.resonz.app.model.PresetDefaults
import com.resonz.app.model.SessionConfig
import kotlin.math.abs

object ConfigModificationChecker {
    fun isModified(config: SessionConfig): Boolean {
        val preset = PresetDefaults.byId(config.presetId)
        return abs(config.beatHz - preset.beatHz) > 0.15 ||
            config.driftLevel != preset.driftLevel ||
            config.timePreset != preset.timePreset ||
            abs(config.toneNormalized - preset.toneNormalized) > 0.08f ||
            config.watchSyncEnabled != preset.watchSyncEnabled
    }
}
