package com.resonz.app.audio

class OnePoleHighPass {
    private var prevX = 0f
    private var prevY = 0f
    private var alpha = 0.995f

    fun setAlpha(value: Float) {
        alpha = value
    }

    fun process(x: Float): Float {
        val y = alpha * (prevY + x - prevX)
        prevX = x
        prevY = y
        return y
    }

    fun reset() {
        prevX = 0f
        prevY = 0f
    }
}
