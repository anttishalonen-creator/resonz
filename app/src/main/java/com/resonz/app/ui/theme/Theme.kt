package com.resonz.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = ResonzColors.NavyPrimary,
    secondary = ResonzColors.NavySecondary,
    background = ResonzColors.BgPrimary,
    surface = ResonzColors.BgCard,
    onPrimary = ResonzColors.TextOnNavy,
    onSecondary = ResonzColors.TextOnNavy,
    onBackground = ResonzColors.TextPrimary,
    onSurface = ResonzColors.TextPrimary,
)

@Composable
fun ResonzTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, typography = AppTypography, content = content)
}
