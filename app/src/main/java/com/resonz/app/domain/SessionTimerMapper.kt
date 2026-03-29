package com.resonz.app.domain

import com.resonz.app.model.TimePreset

fun timePresetToSeconds(preset: TimePreset): Int = when (preset) {
    TimePreset.MIN_20 -> 1200
    TimePreset.MIN_30 -> 1800
    TimePreset.MIN_45 -> 2700
    TimePreset.MIN_90 -> 5400
    TimePreset.ALL_NIGHT -> 28800
}

fun timePresetToLabel(preset: TimePreset): String = when (preset) {
    TimePreset.MIN_20 -> "20 Min"
    TimePreset.MIN_30 -> "30 Min"
    TimePreset.MIN_45 -> "45 Min"
    TimePreset.MIN_90 -> "90 Min"
    TimePreset.ALL_NIGHT -> "All Night"
}
