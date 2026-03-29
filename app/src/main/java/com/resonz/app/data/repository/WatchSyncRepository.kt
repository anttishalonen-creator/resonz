package com.resonz.app.data.repository

import com.resonz.app.model.WatchHeartRateSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WatchSyncRepository {
    private val samples = MutableStateFlow<List<WatchHeartRateSample>>(emptyList())
    fun observeSamples(): StateFlow<List<WatchHeartRateSample>> = samples
    fun push(sample: WatchHeartRateSample) { samples.value = (samples.value + sample).takeLast(64) }
}
