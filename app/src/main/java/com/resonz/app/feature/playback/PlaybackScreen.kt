package com.resonz.app.feature.playback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.resonz.app.ui.theme.*
import com.resonz.app.util.formatBeatHz

@Composable
fun PlaybackScreen(
    onBack: () -> Unit,
    viewModel: PlaybackViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ResonzColors.BgPrimary)
            .padding(horizontal = ResonzSpacing.ScreenHorizontal)
            .padding(top = ResonzSpacing.TopPadding, bottom = ResonzSpacing.BottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(onClick = {
                viewModel.stop()
                onBack()
            }) {
                Text("← Back", color = ResonzColors.NavyPrimary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val progress = if (uiState.totalSec > 0) {
            (uiState.elapsedSec.toFloat() / uiState.totalSec).coerceIn(0f, 1f)
        } else 0f

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = ResonzColors.SliderTrackActive,
            trackColor = ResonzColors.SliderTrackInactive,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = formatTime(uiState.elapsedSec) + " / " + formatTime(uiState.totalSec),
            color = ResonzColors.TextSecondary,
            fontSize = ResonzType.SecondaryLabel
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = uiState.presetName,
            color = ResonzColors.TextPrimary,
            fontSize = ResonzType.ScreenTitle,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = formatBeatHz(uiState.beatHz),
            color = ResonzColors.NavyPrimary,
            fontSize = ResonzType.HeroValue,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.togglePause() },
            modifier = Modifier.size(80.dp).clip(CircleShape),
            colors = ButtonDefaults.buttonColors(
                containerColor = ResonzColors.NavyPrimary,
                contentColor = ResonzColors.TextOnNavy
            ),
            shape = CircleShape
        ) {
            Text(
                text = if (uiState.isPaused) "▶" else "❚❚",
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = {
                viewModel.stop()
                onBack()
            },
            modifier = Modifier.size(56.dp)
        ) {
            Text(
                text = "■",
                color = ResonzColors.ErrorSoft,
                fontSize = 32.sp
            )
        }

        Spacer(modifier = Modifier.height(ResonzSpacing.BottomPadding))
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(mins, secs)
}
