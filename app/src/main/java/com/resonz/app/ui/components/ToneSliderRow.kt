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
fun ToneSliderRow(value: Float, valueText: String, onValueChange: (Float) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeaderRow("TONE", valueText)
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = ResonzColors.SliderThumb,
                activeTrackColor = ResonzColors.SliderTrackActive,
                inactiveTrackColor = ResonzColors.SliderTrackInactive,
            ),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("WARM", color = ResonzColors.TextSecondary, fontSize = ResonzType.SmallLabel)
            Text("BRIGHT", color = ResonzColors.TextSecondary, fontSize = ResonzType.SmallLabel)
        }
    }
}
