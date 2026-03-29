package com.resonz.app.domain

import com.resonz.app.model.ProfileType

object DriftPathCalculator {
    fun beatAtElapsed(anchorBeatHz: Double, profileType: ProfileType, driftAmount: Float, elapsedSec: Int, durationSec: Int, watchNudgeHz: Double = 0.0): Double {
        if (durationSec <= 0 || driftAmount <= 0f) return (anchorBeatHz + watchNudgeHz).coerceAtLeast(0.5)
        val progress = (elapsedSec.toDouble() / durationSec.toDouble()).coerceIn(0.0, 1.0)
        val base = when (profileType) {
            ProfileType.STABLE, ProfileType.FOCUS_STABLE -> anchorBeatHz
            ProfileType.DESCEND_SOFT -> anchorBeatHz - (anchorBeatHz - 1.5) * driftAmount * progress
            ProfileType.DESCEND_ACTIVE -> anchorBeatHz - (anchorBeatHz - 2.0) * driftAmount * progress
            ProfileType.GENTLE_AROUND_ANCHOR -> anchorBeatHz - (anchorBeatHz * 0.08) * driftAmount * progress
        }
        return (base + watchNudgeHz).coerceAtLeast(0.5)
    }
}
