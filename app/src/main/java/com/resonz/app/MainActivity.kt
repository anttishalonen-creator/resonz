package com.resonz.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.resonz.app.navigation.AppNavGraph
import com.resonz.app.ui.theme.ResonzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResonzTheme {
                AppNavGraph()
            }
        }
    }
}
