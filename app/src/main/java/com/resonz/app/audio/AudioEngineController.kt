package com.resonz.app.audio

import com.resonz.app.model.AudioTargets
import com.resonz.app.model.SessionConfig

interface AudioEngineController {
    fun prepare(config: SessionConfig)
    fun start(config: SessionConfig)
    fun pause()
    fun resume()
    fun stop()
    fun release()
    fun updateTargets(targets: AudioTargets)
    fun currentEngineState(): EngineState
}
