package com.resonz.app.audio

import kotlin.math.tanh

object SoftClipper {
    fun process(x: Float): Float {
        val y = tanh(1.15f * x)
        val norm = tanh(1.15f)
        return (y / norm).coerceIn(-0.95f, 0.95f)
    }
}
