package com.resonz.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.resonz.app.ui.theme.ResonzColors
import com.resonz.app.ui.theme.ResonzType

@Composable
fun WatchSyncRow(enabled: Boolean, statusText: String?, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Watch Sync", color = ResonzColors.TextPrimary, fontSize = ResonzType.SectionLabel)
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ResonzColors.BgCard,
                    checkedTrackColor = ResonzColors.ToggleOn,
                    uncheckedThumbColor = ResonzColors.BgCard,
                    uncheckedTrackColor = ResonzColors.ToggleOff,
                ),
            )
        }
        if (!statusText.isNullOrBlank()) {
            Text(statusText, color = ResonzColors.TextSecondary, fontSize = ResonzType.StatusText)
        }
    }
}
