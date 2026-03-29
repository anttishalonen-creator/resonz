package com.resonz.app.model

object PresetDefaults {
    fun deepSleep() = SessionConfig(PresetId.DEEP_SLEEP, 3.0, DriftLevel.LOW, TimePreset.MIN_90, 0.18f, false, false)
    fun driftToSleep() = SessionConfig(PresetId.DRIFT_TO_SLEEP, 8.0, DriftLevel.MEDIUM, TimePreset.MIN_45, 0.22f, false, false)
    fun focus() = SessionConfig(PresetId.FOCUS, 40.0, DriftLevel.OFF, TimePreset.MIN_30, 0.62f, false, false)
    fun earthSync() = SessionConfig(PresetId.EARTH_SYNC, 7.8, DriftLevel.LOW, TimePreset.MIN_30, 0.20f, false, false)
    fun byId(id: PresetId): SessionConfig = when (id) {
        PresetId.DEEP_SLEEP -> deepSleep()
        PresetId.DRIFT_TO_SLEEP -> driftToSleep()
        PresetId.FOCUS -> focus()
        PresetId.EARTH_SYNC -> earthSync()
    }
}
