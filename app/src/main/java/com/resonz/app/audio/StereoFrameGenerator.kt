package com.resonz.app.audio

class StereoFrameGenerator {
    private val leftOsc = Oscillator()
    private val rightOsc = Oscillator()
    private val bodyOsc = Oscillator()
    fun nextFrame(params: SynthParams): Pair<Float, Float> {
        val leftFreq = params.carrierCenterHz - params.beatHz / 2.0
        val rightFreq = params.carrierCenterHz + params.beatHz / 2.0
        val left = leftOsc.nextSample(leftFreq)
        val right = rightOsc.nextSample(rightFreq)
        val body = bodyOsc.nextSample(params.carrierCenterHz / 2.0) * params.bodyMix
        return (left + body) * params.masterGain to (right + body) * params.masterGain
    }
}
