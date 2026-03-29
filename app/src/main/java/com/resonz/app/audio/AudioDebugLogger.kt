package com.resonz.app.audio

import android.util.Log

object AudioDebugLogger {
    private const val TAG = "ResonzAudio"
    fun d(message: String) = Log.d(TAG, message)
    fun e(message: String, throwable: Throwable? = null) = Log.e(TAG, message, throwable)
}
