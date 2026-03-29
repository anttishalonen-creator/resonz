package com.resonz.app.domain

import com.resonz.app.model.WatchHeartRateSample

enum class WatchGuidance { HOLD_LIGHTER, RELEASE_DEEPER, STABLE }

data class WatchTrendResult(val guidance: WatchGuidance, val smoothedBpm: Float?, val baselineBpm: Float?, val nudgeHz: Double)

object WatchTrendInterpreter {
    fun interpret(samples: List<WatchHeartRateSample>): WatchTrendResult {
        if (samples.size < 4) return WatchTrendResult(WatchGuidance.STABLE, samples.lastOrNull()?.bpm, null, 0.0)
        val baseline = samples.take(4).map { it.bpm }.average().toFloat()
        val smoothed = samples.takeLast(4).map { it.bpm }.average().toFloat()
        val delta = smoothed - baseline
        return when {
            delta >= 4f -> WatchTrendResult(WatchGuidance.HOLD_LIGHTER, smoothed, baseline, 0.25)
            delta <= -3f -> WatchTrendResult(WatchGuidance.RELEASE_DEEPER, smoothed, baseline, -0.20)
            else -> WatchTrendResult(WatchGuidance.STABLE, smoothed, baseline, 0.0)
        }
    }
}
