package com.resonz.app.audio

import kotlin.math.sin
import kotlin.math.PI

class OceanLayerRenderer(sampleRate: Float) {
    private val sampleRate = sampleRate
    
    private val brownL = BrownNoiseGenerator(1)
    private val brownR = BrownNoiseGenerator(2)
    
    private val hpfL = OnePoleHighPass()
    private val hpfR = OnePoleHighPass()
    
    private val lpfL = OnePoleLowPass(sampleRate, 1500f)
    private val lpfR = OnePoleLowPass(sampleRate, 1500f)
    
    private val bp1L = BiquadBandPass(sampleRate, 360f, 0.8f)
    private val bp1R = BiquadBandPass(sampleRate, 360f, 0.8f)
    private val bp2L = BiquadBandPass(sampleRate, 850f, 0.9f)
    private val bp2R = BiquadBandPass(sampleRate, 850f, 0.9f)
    
    private var lfoPhase1 = 0.0
    private var lfoPhase2 = 0.0
    private var swellPhase = 0.0
    
    private val lfo1Freq = 0.035f
    private val lfo2Freq = 0.07f
    private val swellFreq = 0.05f
    
    init {
        hpfL.setAlpha(0.99f)
        hpfR.setAlpha(0.99f)
    }

    fun nextFrame(oceanMix: Float): Pair<Float, Float> {
        val rawL = brownL.next()
        val rawR = brownR.next()
        
        var signalL = hpfL.process(rawL)
        var signalR = hpfR.process(rawR)
        
        signalL = lpfL.process(signalL)
        signalR = lpfR.process(signalR)
        
        val lfo1 = (sin(lfoPhase1 * 2.0 * PI) * 0.15 + 1.0).toFloat()
        val lfo2 = (sin(lfoPhase2 * 2.0 * PI) * 0.15 + 1.0).toFloat()
        
        lfoPhase1 += lfo1Freq / sampleRate
        lfoPhase2 += lfo2Freq / sampleRate
        if (lfoPhase1 >= 1.0) lfoPhase1 -= 1.0
        if (lfoPhase2 >= 1.0) lfoPhase2 -= 1.0
        
        val bp1L_mod = BiquadBandPass(sampleRate, 360f * lfo1, 0.8f)
        val bp1R_mod = BiquadBandPass(sampleRate, 360f * lfo1, 0.8f)
        val bp2L_mod = BiquadBandPass(sampleRate, 850f * lfo2, 0.9f)
        val bp2R_mod = BiquadBandPass(sampleRate, 850f * lfo2, 0.9f)
        
        val filteredL = bp1L_mod.process(signalL) + bp2L_mod.process(signalL)
        val filteredR = bp1R_mod.process(signalR) + bp2R_mod.process(signalR)
        
        val swell = (sin(swellPhase * 2.0 * PI) * 0.3 + 0.7).toFloat()
        swellPhase += swellFreq / sampleRate
        if (swellPhase >= 1.0) swellPhase -= 1.0
        
        val outputL = filteredL * oceanMix * swell
        val outputR = filteredR * oceanMix * swell
        
        return outputL to outputR
    }

    fun reset() {
        brownL.reset()
        brownR.reset()
        hpfL.reset()
        hpfR.reset()
        lpfL.reset()
        lpfR.reset()
        lfoPhase1 = 0.0
        lfoPhase2 = 0.0
        swellPhase = 0.0
    }
}
