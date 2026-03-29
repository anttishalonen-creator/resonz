package com.resonz.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.resonz.app.feature.advanced.AdvancedScreen
import com.resonz.app.feature.main.MainScreen
import com.resonz.app.feature.playback.PlaybackScreen
import com.resonz.app.feature.settings.SettingsScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destinations.MAIN) {
        composable(Destinations.MAIN) {
            MainScreen(
                onOpenPlayback = { navController.navigate(Destinations.PLAYBACK) },
                onOpenAdvanced = { navController.navigate(Destinations.ADVANCED) },
                onOpenSettings = { navController.navigate(Destinations.SETTINGS) },
            )
        }
        composable(Destinations.PLAYBACK) { PlaybackScreen(onBack = { navController.popBackStack() }) }
        composable(Destinations.ADVANCED) { AdvancedScreen(onBack = { navController.popBackStack() }) }
        composable(Destinations.SETTINGS) { SettingsScreen(onBack = { navController.popBackStack() }) }
    }
}
