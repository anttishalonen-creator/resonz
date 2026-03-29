package com.resonz.app.model

data class SessionConfig(
    val presetId: PresetId,
    val beatHz: Double,
    val driftLevel: DriftLevel,
    val timePreset: TimePreset,
    val toneNormalized: Float,
    val watchSyncEnabled: Boolean,
    val isModified: Boolean,
)
