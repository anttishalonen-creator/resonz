package com.resonz.app.audio

class SmoothedValue(
    initial: Float,
    private val smoothingCoeff: Float
) {
    var current: Float = initial
        private set
    private var targetValue: Float = initial

    fun setTarget(value: Float) {
        targetValue = value
    }

    fun reset(value: Float) {
        current = value
        targetValue = value
    }

    fun next(): Float {
        current += (targetValue - current) * smoothingCoeff
        return current
    }
}
