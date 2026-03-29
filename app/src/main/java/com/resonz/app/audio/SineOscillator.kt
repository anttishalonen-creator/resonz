package com.resonz.app.audio

import kotlin.math.PI
import kotlin.math.sin

class SineOscillator {
    private var phase = 0.0

    fun next(freqHz: Float, sampleRate: Float): Float {
        val out = sin(phase * 2.0 * PI).toFloat()
        phase += freqHz / sampleRate
        if (phase >= 1.0) phase -= 1.0
        return out
    }

    fun reset() {
        phase = 0.0
    }
}
