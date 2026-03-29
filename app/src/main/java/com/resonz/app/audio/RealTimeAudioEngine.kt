package com.resonz.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.resonz.app.model.PresetId
import com.resonz.app.model.ProfileType
import com.resonz.app.model.SessionConfig
import com.resonz.app.model.TimePreset
import com.resonz.app.model.DriftLevel
import kotlinx.coroutines.*
import com.resonz.app.session.SoundMode

class RealTimeAudioEngine : AudioEngineController {
    private var state: EngineState = EngineState.IDLE
    private var renderLoopActive = true
    
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private val sampleRate = 48000
    private val bufferSize = 2048
    
    private val leftOsc = SineOscillator()
    private val rightOsc = SineOscillator()
    private val h2LeftOsc = SineOscillator()
    private val h2RightOsc = SineOscillator()
    private val h3LeftOsc = SineOscillator()
    private val h3RightOsc = SineOscillator()
    private val bodyOsc = SineOscillator()
    
    private val oceanRenderer = OceanLayerRenderer(sampleRate.toFloat())
    
    private val dcBlockL = DcBlocker()
    private val dcBlockR = DcBlocker()
    
    private val smoothedBeat = SmoothedValue(3.0f, 0.02f)
    private val smoothedCarrier = SmoothedValue(170f, 0.02f)
    private val smoothedBodyMix = SmoothedValue(0f, 0.02f)
    private val smoothedOceanMix = SmoothedValue(0f, 0.02f)
    private val smoothedHarmonic2 = SmoothedValue(0f, 0.02f)
    private val smoothedHarmonic3 = SmoothedValue(0f, 0.02f)
    private val smoothedMasterGain = SmoothedValue(0f, 0.01f)
    
    private var currentPresetId = PresetId.DEEP_SLEEP
    private var currentSoundMode = SoundMode.OCEAN
    private var sessionStartTime = 0L
    private var sessionDurationSec = 0
    private var profileType = ProfileType.STABLE
    private var driftAmount = 0f
    private var anchorBeat = 3.0f
    
    private val masterGainTarget = 0.26f

    override fun prepare(config: SessionConfig) {
        loadFromConfig(config)
        state = EngineState.IDLE
    }

    private fun loadFromConfig(config: SessionConfig) {
        currentPresetId = config.presetId
        sessionDurationSec = timeToSeconds(config.timePreset)
        profileType = getProfileType(config.presetId)
        driftAmount = driftLevelToAmount(config.driftLevel)
        anchorBeat = config.beatHz.toFloat()
        
        val profile = PresetAudioProfiles.fromPreset(config.presetId)
        smoothedCarrier.setTarget(profile.carrierHz)
        smoothedBeat.setTarget(profile.beatHz)
        smoothedBodyMix.setTarget(profile.bodyMix)
        smoothedOceanMix.setTarget(profile.oceanMix)
        smoothedHarmonic2.setTarget(profile.harmonic2Mix)
        smoothedHarmonic3.setTarget(profile.harmonic3Mix)
        
        smoothedCarrier.reset(profile.carrierHz)
        smoothedBeat.reset(profile.beatHz)
        smoothedBodyMix.reset(profile.bodyMix)
        smoothedOceanMix.reset(profile.oceanMix)
        smoothedHarmonic2.reset(profile.harmonic2Mix)
        smoothedHarmonic3.reset(profile.harmonic3Mix)
    }

    override fun start(config: SessionConfig) {
        loadFromConfig(config)
        
        sessionStartTime = System.currentTimeMillis()
        
        smoothedMasterGain.setTarget(0f)
        smoothedMasterGain.reset(0f)
        smoothedMasterGain.setTarget(masterGainTarget)
        
        startAudioPlayback()
        state = EngineState.RUNNING
    }

    private fun startAudioPlayback() {
        val bufferSizeBytes = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_FLOAT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(bufferSizeBytes, bufferSize * 8))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        playbackJob = scope.launch {
            val buffer = FloatArray(bufferSize * 2)
            while (isActive && renderLoopActive) {
                generateAudioBuffer(buffer)
                audioTrack?.write(buffer, 0, buffer.size, AudioTrack.WRITE_BLOCKING)
                
                when (state) {
                    EngineState.STOPPED -> {
                        renderLoopActive = false
                    }
                    else -> { }
                }
            }
        }
    }

    private fun generateAudioBuffer(buffer: FloatArray) {
        val beat = smoothedBeat.next()
        val carrier = smoothedCarrier.next()
        val bodyMix = smoothedBodyMix.next()
        val oceanMix = smoothedOceanMix.next()
        val h2Mix = smoothedHarmonic2.next()
        val h3Mix = smoothedHarmonic3.next()
        val masterGain = smoothedMasterGain.next()
        
        for (i in buffer.indices step 2) {
            val leftFreq = carrier - beat * 0.5f
            val rightFreq = carrier + beat * 0.5f
            
            val leftCore = leftOsc.next(leftFreq, sampleRate.toFloat())
            val rightCore = rightOsc.next(rightFreq, sampleRate.toFloat())
            
            val leftH2 = h2LeftOsc.next(leftFreq * 2f, sampleRate.toFloat()) * h2Mix
            val rightH2 = h2RightOsc.next(rightFreq * 2f, sampleRate.toFloat()) * h2Mix
            val leftH3 = h3LeftOsc.next(leftFreq * 3f, sampleRate.toFloat()) * h3Mix
            val rightH3 = h3RightOsc.next(rightFreq * 3f, sampleRate.toFloat()) * h3Mix
            
            val leftMain = (leftCore + leftH2 + leftH3) * 0.26f
            val rightMain = (rightCore + rightH2 + rightH3) * 0.26f
            
            val bodyFreq = carrier * 0.75f
            val body = bodyOsc.next(bodyFreq, sampleRate.toFloat()) * bodyMix
            val bodyL = body * 0.98f
            val bodyR = body * 1.02f
            
            var oceanL = 0f
            var oceanR = 0f
            if (currentSoundMode == SoundMode.OCEAN) {
                val (oL, oR) = oceanRenderer.nextFrame(oceanMix)
                oceanL = oL
                oceanR = oR
            }
            
            var left = leftMain + bodyL + oceanL
            var right = rightMain + bodyR + oceanR
            
            left = dcBlockL.process(left)
            right = dcBlockR.process(right)
            
            left *= masterGain
            right *= masterGain
            
            left = SoftClipper.process(left)
            right = SoftClipper.process(right)
            
            buffer[i] = left
            buffer[i + 1] = right
        }
    }

    override fun pause() {
        smoothedMasterGain.setTarget(0f)
        state = EngineState.PAUSING
    }

    override fun resume() {
        smoothedMasterGain.setTarget(masterGainTarget)
        state = EngineState.RUNNING
    }

    override fun stop() {
        smoothedMasterGain.setTarget(0f)
        state = EngineState.STOPPING
        
        scope.launch {
            delay(2000)
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
            state = EngineState.STOPPED
            renderLoopActive = false
        }
    }

    override fun release() {
        playbackJob?.cancel()
        scope.cancel()
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        state = EngineState.IDLE
    }

    override fun updateTargets(targets: com.resonz.app.model.AudioTargets) {
        smoothedBeat.setTarget(targets.beatHz.toFloat())
        smoothedCarrier.setTarget(targets.carrierCenterHz.toFloat())
        smoothedBodyMix.setTarget(targets.bodyMix)
    }

    override fun currentEngineState(): EngineState = state

    fun setSoundMode(mode: SoundMode) {
        currentSoundMode = mode
    }
    
    fun updateBeatTarget(beatHz: Float) {
        smoothedBeat.setTarget(beatHz)
    }
    
    fun updateToneTarget(toneNormalized: Float) {
        val carrier = toneToCarrier(toneNormalized)
        val body = toneToBodyMix(toneNormalized)
        val ocean = toneToOceanMix(toneNormalized)
        smoothedCarrier.setTarget(carrier)
        smoothedBodyMix.setTarget(body)
        smoothedOceanMix.setTarget(ocean)
    }
    
    private fun toneToOceanMix(tone: Float): Float = when {
        tone < 0.33f -> 0.06f
        tone < 0.66f -> 0.04f
        else -> 0.02f
    }
    
    private fun toneToCarrier(tone: Float): Float = when {
        tone < 0.33f -> 140f + tone * 120f
        tone < 0.66f -> 180f + (tone - 0.33f) * 180f
        else -> 240f + (tone - 0.66f) * 120f
    }
    
    private fun toneToBodyMix(tone: Float): Float = when {
        tone < 0.33f -> 0.25f - tone * 0.15f
        tone < 0.66f -> 0.20f
        else -> 0.15f - (tone - 0.66f) * 0.3f
    }.coerceIn(0.05f, 0.30f)

    private fun timeToSeconds(timePreset: TimePreset): Int = when (timePreset) {
        TimePreset.MIN_20 -> 1200
        TimePreset.MIN_30 -> 1800
        TimePreset.MIN_45 -> 2700
        TimePreset.MIN_90 -> 5400
        TimePreset.ALL_NIGHT -> 28800
    }

    private fun driftLevelToAmount(level: DriftLevel): Float = when (level) {
        DriftLevel.OFF -> 0f
        DriftLevel.LOW -> 0.33f
        DriftLevel.MEDIUM -> 0.66f
        DriftLevel.HIGH -> 1f
    }

    private fun getProfileType(presetId: PresetId): ProfileType = when (presetId) {
        PresetId.DEEP_SLEEP -> ProfileType.DESCEND_SOFT
        PresetId.DRIFT_TO_SLEEP -> ProfileType.DESCEND_ACTIVE
        PresetId.FOCUS -> ProfileType.FOCUS_STABLE
        PresetId.EARTH_SYNC -> ProfileType.GENTLE_AROUND_ANCHOR
    }
}
