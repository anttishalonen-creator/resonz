package com.resonz.app.audio

import kotlin.random.Random

class BrownNoiseGenerator(seed: Int = 42) {
    private val random = Random(seed)
    private var state = 0f

    fun next(): Float {
        val white = random.nextFloat() * 2f - 1f
        state = (state + 0.02f * white).coerceIn(-1f, 1f)
        return state * 3.5f
    }

    fun reset() {
        state = 0f
    }
}
