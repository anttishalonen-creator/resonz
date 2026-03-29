package com.resonz.app.audio

import kotlin.math.PI
import kotlin.math.exp

class OnePoleLowPass(sampleRate: Float, cutoffHz: Float) {
    private var a = 0f
    private var z = 0f

    init { setCutoff(sampleRate, cutoffHz) }

    fun setCutoff(sampleRate: Float, cutoffHz: Float) {
        a = (1f - exp((-2.0 * PI * cutoffHz / sampleRate).toFloat())).toFloat()
    }

    fun process(x: Float): Float {
        z += a * (x - z)
        return z
    }

    fun reset() {
        z = 0f
    }
}
