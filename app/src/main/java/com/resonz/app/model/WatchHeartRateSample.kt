package com.resonz.app.model

data class WatchHeartRateSample(val timestampMs: Long, val bpm: Float, val confidence: Float? = null)
