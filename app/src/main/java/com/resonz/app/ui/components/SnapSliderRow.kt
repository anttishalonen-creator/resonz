package com.resonz.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.resonz.app.ui.theme.ResonzColors
import com.resonz.app.ui.theme.ResonzType

@Composable
fun SnapSliderRow(
    label: String,
    valueText: String,
    steps: Int,
    position: Float,
    onPositionChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    captions: List<String> = emptyList(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeaderRow(label, valueText)
        Slider(
            value = position,
            onValueChange = onPositionChange,
            steps = (steps - 2).coerceAtLeast(0),
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = ResonzColors.SliderThumb,
                activeTrackColor = ResonzColors.SliderTrackActive,
                inactiveTrackColor = ResonzColors.SliderTrackInactive,
            ),
        )
        if (captions.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                captions.forEach { Text(it, color = ResonzColors.TextSecondary, fontSize = ResonzType.SmallLabel) }
            }
        }
    }
}
