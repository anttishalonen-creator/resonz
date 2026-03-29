package com.resonz.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.resonz.app.ui.theme.ResonzColors
import com.resonz.app.ui.theme.ResonzShapes
import com.resonz.app.ui.theme.ResonzType

@Composable
fun ResonzLogo(showWordmark: Boolean = true, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(ResonzShapes.CardRadius))
                .background(ResonzColors.BgCard)
                .border(1.dp, ResonzColors.LineSoft.copy(alpha = 0.35f), RoundedCornerShape(ResonzShapes.CardRadius)),
            contentAlignment = Alignment.Center,
        ) {
            Text("R", color = ResonzColors.NavyPrimary, fontSize = ResonzType.Wordmark, fontWeight = FontWeight.SemiBold)
        }
        if (showWordmark) {
            Text("Resonz", color = ResonzColors.TextPrimary, fontSize = ResonzType.Wordmark, fontWeight = FontWeight.Medium)
        }
    }
}
