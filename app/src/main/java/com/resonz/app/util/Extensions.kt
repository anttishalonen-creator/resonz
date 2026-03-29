package com.resonz.app.util

fun Float.clamp01(): Float = coerceIn(0f, 1f)
