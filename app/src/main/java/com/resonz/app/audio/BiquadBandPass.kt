package com.resonz.app.audio

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class BiquadBandPass(sampleRate: Float, centerHz: Float, q: Float) {
    private var a0 = 1f
    private var a1 = 0f
    private var a2 = 0f
    private var b1 = 0f
    private var b2 = 0f
    
    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f

    init {
        setBandPass(sampleRate, centerHz, q)
    }

    fun setBandPass(sampleRate: Float, centerHz: Float, q: Float) {
        val w0 = 2.0 * PI * centerHz / sampleRate
        val alpha = sin(w0) / (2.0 * q)
        
        val b0 = alpha.toFloat()
        val b1 = 0.0f
        val b2 = (-alpha).toFloat()
        val a0 = (1.0 + alpha).toFloat()
        val a1 = (-2.0 * cos(w0)).toFloat()
        val a2 = (1.0 - alpha).toFloat()
        
        this.a0 = b0 / a0
        this.a1 = b1 / a0
        this.a2 = b2 / a0
        this.b1 = a1 / a0
        this.b2 = a2 / a0
    }

    fun process(x: Float): Float {
        val y = a0 * x + a1 * x1 + a2 * x2 - b1 * y1 - b2 * y2
        x2 = x1
        x1 = x
        y2 = y1
        y1 = y
        return y
    }

    fun reset() {
        x1 = 0f
        x2 = 0f
        y1 = 0f
        y2 = 0f
    }
}
