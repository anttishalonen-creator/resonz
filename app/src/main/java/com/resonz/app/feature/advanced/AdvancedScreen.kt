package com.resonz.app.feature.advanced

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.resonz.app.ui.theme.ResonzColors

@Composable
fun AdvancedScreen(onBack: () -> Unit) { Column(Modifier.fillMaxSize().background(ResonzColors.BgPrimary).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) { TextButton(onClick = onBack) { Text("Back") }; Text("Advanced screen stub") } }
