package com.resonz.app.audio

import com.resonz.app.model.PresetId

data class PresetAudioProfile(
    val carrierHz: Float,
    val beatHz: Float,
    val bodyMix: Float,
    val oceanMix: Float,
    val harmonic2Mix: Float,
    val harmonic3Mix: Float
)

object PresetAudioProfiles {
    fun fromPreset(id: PresetId): PresetAudioProfile = when (id) {
        PresetId.DEEP_SLEEP -> PresetAudioProfile(
            carrierHz = 170f,
            beatHz = 3.0f,
            bodyMix = 0.00f,
            oceanMix = 0.045f,
            harmonic2Mix = 0f,
            harmonic3Mix = 0f
        )
        PresetId.DRIFT_TO_SLEEP -> PresetAudioProfile(
            carrierHz = 185f,
            beatHz = 8.0f,
            bodyMix = 0.015f,
            oceanMix = 0.05f,
            harmonic2Mix = 0f,
            harmonic3Mix = 0f
        )
        PresetId.EARTH_SYNC -> PresetAudioProfile(
            carrierHz = 170f,
            beatHz = 7.83f,
            bodyMix = 0.00f,
            oceanMix = 0.04f,
            harmonic2Mix = 0f,
            harmonic3Mix = 0f
        )
        PresetId.FOCUS -> PresetAudioProfile(
            carrierHz = 230f,
            beatHz = 14f,
            bodyMix = 0.03f,
            oceanMix = 0.02f,
            harmonic2Mix = 0.04f,
            harmonic3Mix = 0.015f
        )
    }
}
