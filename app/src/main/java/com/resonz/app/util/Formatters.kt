package com.resonz.app.util

import java.util.Locale

fun formatBeatHz(hz: Double): String = if (hz < 1.0) String.format(Locale.US, "%.2f Hz", hz) else String.format(Locale.US, "%.1f Hz", hz)
