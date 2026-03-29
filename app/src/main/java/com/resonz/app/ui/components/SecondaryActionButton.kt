package com.resonz.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.resonz.app.ui.theme.ResonzColors
import com.resonz.app.ui.theme.ResonzShapes
import com.resonz.app.ui.theme.ResonzType

@Composable
fun SecondaryActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(58.dp),
        shape = RoundedCornerShape(ResonzShapes.ButtonRadius),
        border = BorderStroke(1.dp, ResonzColors.LineSoft.copy(alpha = 0.35f)),
        colors = ButtonDefaults.buttonColors(containerColor = ResonzColors.TileInactive, contentColor = ResonzColors.TextPrimary),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(text, fontSize = ResonzType.ButtonLabel, fontWeight = FontWeight.Medium)
    }
}
