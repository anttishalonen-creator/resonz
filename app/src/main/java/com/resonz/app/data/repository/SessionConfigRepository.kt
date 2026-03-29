package com.resonz.app.data.repository

import com.resonz.app.model.PresetDefaults
import com.resonz.app.model.SessionConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionConfigRepository {
    private val flow = MutableStateFlow(PresetDefaults.deepSleep())
    fun observe(): StateFlow<SessionConfig> = flow
    suspend fun save(config: SessionConfig) { flow.value = config }
    suspend fun load(): SessionConfig = flow.value
}
