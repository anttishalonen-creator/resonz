package com.resonz.app.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.resonz.app.model.PresetId
import com.resonz.app.model.WatchSyncStatus
import com.resonz.app.ui.components.*
import com.resonz.app.ui.theme.*
import com.resonz.app.util.formatBeatHz

@Composable
fun MainScreen(
    onOpenPlayback: () -> Unit,
    onOpenAdvanced: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: MainViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.onAction(MainAction.LoadInitialState) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ResonzColors.BgPrimary)
            .padding(horizontal = ResonzSpacing.ScreenHorizontal)
            .padding(top = ResonzSpacing.TopPadding, bottom = ResonzSpacing.BottomPadding),
    ) {
        ResonzLogo(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(ResonzSpacing.GridGap)) {
            PresetButton("Deep Sleep", uiState.config.presetId == PresetId.DEEP_SLEEP, { viewModel.onAction(MainAction.SelectPreset(PresetId.DEEP_SLEEP)) }, Modifier.weight(1f))
            PresetButton("Drift to Sleep", uiState.config.presetId == PresetId.DRIFT_TO_SLEEP, { viewModel.onAction(MainAction.SelectPreset(PresetId.DRIFT_TO_SLEEP)) }, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(ResonzSpacing.GridGap))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(ResonzSpacing.GridGap)) {
            PresetButton("Focus", uiState.config.presetId == PresetId.FOCUS, { viewModel.onAction(MainAction.SelectPreset(PresetId.FOCUS)) }, Modifier.weight(1f))
            PresetButton("Earth Sync", uiState.config.presetId == PresetId.EARTH_SYNC, { viewModel.onAction(MainAction.SelectPreset(PresetId.EARTH_SYNC)) }, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(ResonzSpacing.SectionGapLarge))
        SectionHeaderRow("BEAT", formatBeatHz(uiState.config.beatHz))
        Spacer(modifier = Modifier.height(ResonzSpacing.LabelToSliderGap))
        HeroBeatSlider(viewModel.beatNormalized(), { viewModel.onAction(MainAction.SetBeatNormalized(it)) })

        Spacer(modifier = Modifier.height(24.dp))
        SnapSliderRow("DRIFT", uiState.config.driftLevel.name.lowercase().replaceFirstChar { it.uppercase() }, 4, viewModel.driftPosition(), { viewModel.setDriftFromPosition(it) }, captions = listOf("Off", "Low", "Med", "High"))
        Spacer(modifier = Modifier.height(16.dp))
        SnapSliderRow("TIME", viewModel.timeLabel(), 5, viewModel.timePosition(), { viewModel.setTimeFromPosition(it) }, captions = listOf("20", "30", "45", "90", "Night"))
        Spacer(modifier = Modifier.height(16.dp))
        ToneSliderRow(uiState.config.toneNormalized, viewModel.toneLabel(), { viewModel.onAction(MainAction.SetToneNormalized(it)) })

        Spacer(modifier = Modifier.height(20.dp))
        val statusText = when (uiState.watchSyncStatus) {
            WatchSyncStatus.OFF -> "Off"
            WatchSyncStatus.WAITING -> "Waiting for watch"
            WatchSyncStatus.CONNECTED -> "Watch connected"
            WatchSyncStatus.ACTIVE -> "Following heart-rate trend"
            WatchSyncStatus.DISCONNECTED -> "Watch disconnected"
            WatchSyncStatus.UNSUPPORTED_FOR_PRESET -> "Not active for this mode"
        }
        WatchSyncRow(uiState.config.watchSyncEnabled, statusText, { viewModel.onAction(MainAction.ToggleWatchSync(it)) })

        uiState.statusMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = ResonzColors.TextSecondary, fontSize = ResonzType.StatusText)
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        PlayButton(
            text = "START SESSION",
            onClick = { 
                viewModel.generateMoodBasedAudio()
                viewModel.onAction(MainAction.PlayPause)
                onOpenPlayback()
            }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(ResonzSpacing.GridGap)) {
            SecondaryActionButton("TUNING", onOpenAdvanced, Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
