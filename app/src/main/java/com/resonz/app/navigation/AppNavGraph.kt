package com.resonz.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.resonz.app.feature.advanced.AdvancedScreen
import com.resonz.app.feature.main.MainScreen
import com.resonz.app.feature.main.MainViewModel
import com.resonz.app.feature.playback.PlaybackScreen
import com.resonz.app.feature.playback.PlaybackViewModel
import com.resonz.app.feature.settings.SettingsScreen
import com.resonz.app.session.SessionCoordinator

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val coordinator = mainViewModel.coordinator
    
    NavHost(navController = navController, startDestination = Destinations.MAIN) {
        composable(Destinations.MAIN) {
            MainScreen(
                viewModel = mainViewModel,
                onOpenPlayback = { navController.navigate(Destinations.PLAYBACK) },
                onOpenAdvanced = { navController.navigate(Destinations.ADVANCED) },
                onOpenSettings = { navController.navigate(Destinations.SETTINGS) },
            )
        }
        composable(Destinations.PLAYBACK) {
            PlaybackScreen(
                coordinator = coordinator,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.ADVANCED) { AdvancedScreen(onBack = { navController.popBackStack() }) }
        composable(Destinations.SETTINGS) { SettingsScreen(onBack = { navController.popBackStack() }) }
    }
}
