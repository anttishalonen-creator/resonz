package com.resonz.app.model

data class AudioTargets(
    val beatHz: Double,
    val carrierCenterHz: Double,
    val bodyMix: Float,
    val brightness: Float,
    val masterGain: Float,
    val profileType: ProfileType,
    val driftAmount: Float,
    val sessionDurationSec: Int,
)
