package com.resonz.app.domain

import com.resonz.app.model.ToneLabel

fun toneNormalizedToLabel(value: Float): ToneLabel = when {
    value <= 0.33f -> ToneLabel.WARM
    value <= 0.66f -> ToneLabel.NEUTRAL
    else -> ToneLabel.BRIGHT
}

fun toneNormalizedToCarrierCenter(value: Float): Double = 140.0 + (320.0 - 140.0) * value.coerceIn(0f, 1f)
fun toneNormalizedToBodyMix(value: Float): Float = (0.42f - 0.22f * value.coerceIn(0f, 1f)).coerceIn(0.12f, 0.42f)
fun toneNormalizedToBrightness(value: Float): Float = value.coerceIn(0f, 1f)
