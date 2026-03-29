package com.resonz.app.audio

import kotlin.math.PI
import kotlin.math.sin

class Oscillator(private var sampleRate: Double = 48000.0) {
    private var phase: Double = 0.0
    fun nextSample(frequencyHz: Double): Float {
        val value = sin(phase * 2.0 * PI).toFloat()
        phase += frequencyHz / sampleRate
        if (phase >= 1.0) phase -= 1.0
        return value
    }
}
