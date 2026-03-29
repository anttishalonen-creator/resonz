package com.resonz.app.data.prefs

import com.resonz.app.model.*

object SessionConfigSerializer {
    fun serialize(config: SessionConfig): String = listOf(config.presetId.name, config.beatHz, config.driftLevel.name, config.timePreset.name, config.toneNormalized, config.watchSyncEnabled, config.isModified).joinToString("|")
    fun deserialize(raw: String): SessionConfig? {
        val parts = raw.split("|")
        if (parts.size != 7) return null
        return runCatching {
            SessionConfig(PresetId.valueOf(parts[0]), parts[1].toDouble(), DriftLevel.valueOf(parts[2]), TimePreset.valueOf(parts[3]), parts[4].toFloat(), parts[5].toBoolean(), parts[6].toBoolean())
        }.getOrNull()
    }
}
