package com.resonz.app.wear

import com.resonz.app.model.PresetId
import com.resonz.app.model.WatchSyncStatus

object WearSyncStatusMapper {
    fun map(enabled: Boolean, connectionState: WatchConnectionState, presetId: PresetId): WatchSyncStatus {
        if (!enabled) return WatchSyncStatus.OFF
        if (presetId == PresetId.FOCUS) return WatchSyncStatus.UNSUPPORTED_FOR_PRESET
        return when (connectionState) {
            WatchConnectionState.DISCONNECTED, WatchConnectionState.CONNECTING -> WatchSyncStatus.WAITING
            WatchConnectionState.CONNECTED -> WatchSyncStatus.ACTIVE
        }
    }
}
