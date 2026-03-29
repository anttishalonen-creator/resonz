package com.resonz.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.resonz.app.ui.theme.ResonzColors
import com.resonz.app.ui.theme.ResonzType

@Composable
fun SectionHeaderRow(label: String, valueText: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = ResonzColors.TextPrimary, fontSize = ResonzType.SectionLabel, fontWeight = FontWeight.SemiBold)
        Text(valueText, color = ResonzColors.TextPrimary, fontSize = ResonzType.SecondaryLabel)
    }
}
