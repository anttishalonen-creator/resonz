package com.resonz.app.util

fun formatElapsedTime(totalSeconds: Int): String = "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)
