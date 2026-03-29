package com.resonz.app.audio

class ParameterSmoother(initialValue: Double = 0.0, private val smoothing: Double = 0.05) {
    private var current = initialValue
    fun update(target: Double): Double {
        current += (target - current) * smoothing
        return current
    }
    fun value(): Double = current
}
