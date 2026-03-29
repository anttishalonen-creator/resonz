package com.resonz.app.domain

import com.resonz.app.model.*

fun sessionConfigToAudioTargets(config: SessionConfig): AudioTargets {
    val profileType = when (config.presetId) {
        PresetId.DEEP_SLEEP -> ProfileType.DESCEND_SOFT
        PresetId.DRIFT_TO_SLEEP -> ProfileType.DESCEND_ACTIVE
        PresetId.FOCUS -> ProfileType.FOCUS_STABLE
        PresetId.EARTH_SYNC -> ProfileType.GENTLE_AROUND_ANCHOR
    }
    val driftAmount = when (config.driftLevel) {
        DriftLevel.OFF -> 0f
        DriftLevel.LOW -> 0.33f
        DriftLevel.MEDIUM -> 0.66f
        DriftLevel.HIGH -> 1f
    }
    return AudioTargets(
        beatHz = config.beatHz,
        carrierCenterHz = toneNormalizedToCarrierCenter(config.toneNormalized),
        bodyMix = toneNormalizedToBodyMix(config.toneNormalized),
        brightness = toneNormalizedToBrightness(config.toneNormalized),
        masterGain = 0.85f,
        profileType = profileType,
        driftAmount = driftAmount,
        sessionDurationSec = timePresetToSeconds(config.timePreset),
    )
}
