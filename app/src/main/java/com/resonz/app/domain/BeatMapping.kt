package com.resonz.app.domain

import kotlin.math.exp
import kotlin.math.ln

private const val MIN_BEAT = 0.5
private const val MAX_BEAT = 40.0

fun normalizedToBeatHz(value: Float): Double {
    val v = value.coerceIn(0f, 1f).toDouble()
    val minLn = ln(MIN_BEAT)
    val maxLn = ln(MAX_BEAT)
    return exp(minLn + (maxLn - minLn) * v)
}

fun beatHzToNormalized(hz: Double): Float {
    val clamped = hz.coerceIn(MIN_BEAT, MAX_BEAT)
    val minLn = ln(MIN_BEAT)
    val maxLn = ln(MAX_BEAT)
    return ((ln(clamped) - minLn) / (maxLn - minLn)).toFloat()
}
