package com.resonz.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.resonz.app.ui.theme.ResonzColors
import com.resonz.app.ui.theme.ResonzType

@Composable
fun HeroBeatSlider(value: Float, onValueChange: (Float) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
            colors = SliderDefaults.colors(
                thumbColor = ResonzColors.SliderThumb,
                activeTrackColor = ResonzColors.SliderTrackActive,
                inactiveTrackColor = ResonzColors.SliderTrackInactive,
            ),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("DEEP", "DRIFT", "CALM", "FOCUS").forEach {
                Text(it, color = ResonzColors.TextSecondary, fontSize = ResonzType.SmallLabel)
            }
        }
    }
}
