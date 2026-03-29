package com.resonz.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.resonz.app.model.AudioTargets
import com.resonz.app.model.ProfileType
import com.resonz.app.model.SessionConfig
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.cos

class RealTimeAudioEngine : AudioEngineController {
    private var state: EngineState = EngineState.IDLE
    private var lastTargets: AudioTargets? = null
    
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private val sampleRate = 48000
    private val bufferSize = 2048
    
    private var leftPhase = 0.0
    private var rightPhase = 0.0
    private var bodyPhase = 0.0
    private var noisePhase = 0.0
    
    private var targetBeatHz = 8.0
    private var targetCarrierCenter = 180.0
    private var targetBodyMix = 0.15f
    private var targetBrightness = 0.5f
    private var targetMasterGain = 0.0f
    
    private var currentBeatHz = 8.0
    private var currentCarrierCenter = 180.0
    private var currentBodyMix = 0.15f
    private var currentBrightness = 0.5f
    private var currentMasterGain = 0.0f
    
    private var isFadingIn = false
    private var isFadingOut = false
    private var fadeStartTime = 0L
    private val fadeDurationMs = 800L
    
    private var sessionStartTime = 0L
    private var sessionDurationSec = 0
    private var profileType = ProfileType.STABLE
    private var driftAmount = 0f
    private var anchorBeat = 8.0
    
    private var watchHeartRateAdjustment = 0.0

    override fun prepare(config: SessionConfig) {
        state = EngineState.PREPARED
        initializeTargetsFromConfig(config)
    }

    private fun initializeTargetsFromConfig(config: SessionConfig) {
        val targets = AudioTargets(
            beatHz = config.beatHz,
            carrierCenterHz = toneToCarrier(config.toneNormalized),
            bodyMix = toneToBodyMix(config.toneNormalized),
            brightness = config.toneNormalized,
            masterGain = 0.0f,
            profileType = getProfileType(config.presetId),
            driftAmount = driftLevelToAmount(config.driftLevel),
            sessionDurationSec = timeToSeconds(config.timePreset)
        )
        updateTargets(targets)
    }

    override fun start(config: SessionConfig) {
        initializeTargetsFromConfig(config)
        
        sessionStartTime = System.currentTimeMillis()
        sessionDurationSec = timeToSeconds(config.timePreset)
        profileType = getProfileType(config.presetId)
        driftAmount = driftLevelToAmount(config.driftLevel)
        anchorBeat = config.beatHz
        
        targetMasterGain = 0.8f
        isFadingIn = true
        fadeStartTime = System.currentTimeMillis()
        
        startAudioPlayback()
        state = EngineState.PLAYING
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
            while (isActive && state == EngineState.PLAYING) {
                updateParameters()
                generateAudioBuffer(buffer)
                audioTrack?.write(buffer, 0, buffer.size, AudioTrack.WRITE_BLOCKING)
            }
        }
    }

    private fun updateParameters() {
        val now = System.currentTimeMillis()
        
        if (isFadingIn) {
            val elapsed = now - fadeStartTime
            val progress = (elapsed.toFloat() / fadeDurationMs).coerceIn(0f, 1f)
            currentMasterGain = progress * targetMasterGain
            if (progress >= 1f) isFadingIn = false
        } else if (isFadingOut) {
            val elapsed = now - fadeStartTime
            val progress = (elapsed.toFloat() / fadeDurationMs).coerceIn(0f, 1f)
            currentMasterGain = (1f - progress) * targetMasterGain
            if (progress >= 1f) {
                isFadingOut = false
                audioTrack?.stop()
                audioTrack?.release()
                audioTrack = null
                state = EngineState.STOPPED
            }
        }
        
        val smoothing = 0.03
        currentBeatHz += (targetBeatHz - currentBeatHz) * smoothing
        currentCarrierCenter += (targetCarrierCenter - currentCarrierCenter) * smoothing
        currentBodyMix = (currentBodyMix + (targetBodyMix - currentBodyMix) * smoothing).toFloat()
        currentBrightness = (currentBrightness + (targetBrightness - currentBrightness) * smoothing).toFloat()
        
        updateDriftPath()
    }

    private fun updateDriftPath() {
        if (profileType == ProfileType.STABLE || profileType == ProfileType.FOCUS_STABLE || driftAmount == 0f) {
            return
        }
        
        val elapsedSec = (System.currentTimeMillis() - sessionStartTime) / 1000.0
        val progress = (elapsedSec / sessionDurationSec).coerceIn(0.0, 1.0)
        
        val descentAmount = when (profileType) {
            ProfileType.DESCEND_SOFT -> 1.5
            ProfileType.DESCEND_ACTIVE -> 5.5
            ProfileType.GENTLE_AROUND_ANCHOR -> 0.5
            else -> 0.0
        }
        
        val totalDescent = descentAmount * driftAmount
        val driftDelta = totalDescent * progress
        
        var adjustedBeat = anchorBeat - driftDelta + watchHeartRateAdjustment
        adjustedBeat = adjustedBeat.coerceIn(0.5, 40.0)
        
        targetBeatHz = adjustedBeat
    }

    private fun generateAudioBuffer(buffer: FloatArray) {
        for (i in buffer.indices step 2) {
            val leftFreq = currentCarrierCenter - currentBeatHz / 2.0
            val rightFreq = currentCarrierCenter + currentBeatHz / 2.0
            
            val leftSample = sin(leftPhase * 2.0 * PI).toFloat()
            val rightSample = sin(rightPhase * 2.0 * PI).toFloat()
            
            leftPhase += leftFreq / sampleRate
            rightPhase += rightFreq / sampleRate
            if (leftPhase >= 1.0) leftPhase -= 1.0
            if (rightPhase >= 1.0) rightPhase -= 1.0
            
            val bodyFreq = currentCarrierCenter * 0.5
            val bodySample = sin(bodyPhase * 2.0 * PI).toFloat() * currentBodyMix
            bodyPhase += bodyFreq / sampleRate
            if (bodyPhase >= 1.0) bodyPhase -= 1.0
            
            var noise = 0f
            if (currentBrightness > 0.7f) {
                val brightnessFactor = (currentBrightness - 0.7f) / 0.3f
                noise = ((sin(noisePhase * 2.0 * PI * 0.1) + 1f) * 0.5f - 0.5f).toFloat() * 0.02f * brightnessFactor
                noisePhase += 1.0 / sampleRate
                if (noisePhase >= 10.0) noisePhase -= 10.0
            }
            
            val left = (leftSample + bodySample + noise) * currentMasterGain
            val right = (rightSample + bodySample + noise) * currentMasterGain
            
            buffer[i] = left.coerceIn(-1f, 1f)
            buffer[i + 1] = right.coerceIn(-1f, 1f)
        }
    }

    override fun pause() {
        isFadingOut = true
        fadeStartTime = System.currentTimeMillis()
        targetMasterGain = currentMasterGain
        state = EngineState.PAUSED
    }

    override fun resume() {
        targetMasterGain = 0.8f
        isFadingIn = true
        fadeStartTime = System.currentTimeMillis()
        state = EngineState.PLAYING
    }

    override fun stop() {
        isFadingOut = true
        fadeStartTime = System.currentTimeMillis()
        targetMasterGain = currentMasterGain
        state = EngineState.STOPPING
    }

    override fun release() {
        playbackJob?.cancel()
        scope.cancel()
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        state = EngineState.IDLE
    }

    override fun updateTargets(targets: AudioTargets) {
        lastTargets = targets
        targetBeatHz = targets.beatHz
        targetCarrierCenter = targets.carrierCenterHz
        targetBodyMix = targets.bodyMix
        targetBrightness = targets.brightness
        
        if (state == EngineState.IDLE || state == EngineState.PREPARED) {
            currentBeatHz = targets.beatHz
            currentCarrierCenter = targets.carrierCenterHz
            currentBodyMix = targets.bodyMix
            currentBrightness = targets.brightness
        }
    }

    override fun currentEngineState(): EngineState = state

    fun updateWatchHeartRate(bpm: Float) {
        val baselineBpm = 65f
        val delta = bpm - baselineBpm
        
        val adjustment = when {
            delta > 4 -> -0.3
            delta < -3 -> 0.2
            else -> 0.0
        }
        watchHeartRateAdjustment = adjustment.coerceIn(-0.5, 0.5)
    }

    private fun toneToCarrier(tone: Float): Double = when {
        tone < 0.33 -> 140.0 + tone * 120.0
        tone < 0.66 -> 180.0 + (tone - 0.33) * 180.0
        else -> 240.0 + (tone - 0.66) * 120.0
    }

    private fun toneToBodyMix(tone: Float): Float = when {
        tone < 0.33f -> 0.25f - tone * 0.15f
        tone < 0.66f -> 0.20f
        else -> 0.15f - (tone - 0.66f) * 0.3f
    }.coerceIn(0.05f, 0.30f)

    private fun timeToSeconds(timePreset: com.resonz.app.model.TimePreset): Int = when (timePreset) {
        com.resonz.app.model.TimePreset.MIN_20 -> 1200
        com.resonz.app.model.TimePreset.MIN_30 -> 1800
        com.resonz.app.model.TimePreset.MIN_45 -> 2700
        com.resonz.app.model.TimePreset.MIN_90 -> 5400
        com.resonz.app.model.TimePreset.ALL_NIGHT -> 28800
    }

    private fun driftLevelToAmount(level: com.resonz.app.model.DriftLevel): Float = when (level) {
        com.resonz.app.model.DriftLevel.OFF -> 0f
        com.resonz.app.model.DriftLevel.LOW -> 0.33f
        com.resonz.app.model.DriftLevel.MEDIUM -> 0.66f
        com.resonz.app.model.DriftLevel.HIGH -> 1f
    }

    private fun getProfileType(presetId: com.resonz.app.model.PresetId): ProfileType = when (presetId) {
        com.resonz.app.model.PresetId.DEEP_SLEEP -> ProfileType.DESCEND_SOFT
        com.resonz.app.model.PresetId.DRIFT_TO_SLEEP -> ProfileType.DESCEND_ACTIVE
        com.resonz.app.model.PresetId.FOCUS -> ProfileType.FOCUS_STABLE
        com.resonz.app.model.PresetId.EARTH_SYNC -> ProfileType.GENTLE_AROUND_ANCHOR
    }
}
