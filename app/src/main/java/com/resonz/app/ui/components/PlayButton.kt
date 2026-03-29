package com.resonz.app.ui.components

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
import androidx.compose.ui.unit.sp
import com.resonz.app.ui.theme.ResonzColors
import com.resonz.app.ui.theme.ResonzShapes

@Composable
fun PlayButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(64.dp),
        shape = RoundedCornerShape(ResonzShapes.ButtonRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = ResonzColors.PlayButton,
            contentColor = ResonzColors.TextOnNavy,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
