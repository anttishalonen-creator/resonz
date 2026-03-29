package com.resonz.app.wear

import com.resonz.app.model.WatchHeartRateSample

object WearMessageParser {
    fun parseHeartRate(message: String): WatchHeartRateSample? {
        val parts = message.split("|")
        if (parts.size < 2) return null
        return runCatching { WatchHeartRateSample(parts[0].toLong(), parts[1].toFloat(), parts.getOrNull(2)?.toFloatOrNull()) }.getOrNull()
    }
}
