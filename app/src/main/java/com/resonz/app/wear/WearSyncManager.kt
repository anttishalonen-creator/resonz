package com.resonz.app.wear

import com.resonz.app.model.WatchHeartRateSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WearSyncManager {
    private val connection = MutableStateFlow(WatchConnectionState.DISCONNECTED)
    private val latestSample = MutableStateFlow<WatchHeartRateSample?>(null)
    fun connectionState(): StateFlow<WatchConnectionState> = connection
    fun latestHeartRate(): StateFlow<WatchHeartRateSample?> = latestSample
    fun setConnected(connected: Boolean) { connection.value = if (connected) WatchConnectionState.CONNECTED else WatchConnectionState.DISCONNECTED }
    fun pushSample(sample: WatchHeartRateSample) { latestSample.value = sample }
}
