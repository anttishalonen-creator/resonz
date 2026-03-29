package com.resonz.app.audio

class DcBlocker {
    private var prevX = 0f
    private var prevY = 0f
    private val r = 0.995f

    fun process(x: Float): Float {
        val y = x - prevX + r * prevY
        prevX = x
        prevY = y
        return y
    }

    fun reset() {
        prevX = 0f
        prevY = 0f
    }
}
