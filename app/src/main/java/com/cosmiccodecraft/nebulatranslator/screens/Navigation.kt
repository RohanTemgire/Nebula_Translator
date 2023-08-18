package com.cosmiccodecraft.nebulatranslator.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cosmiccodecraft.nebulatranslator.SharedViewModel
import com.cosmiccodecraft.nebulatranslator.screens.translation_screen.TranslationPage

@Composable
fun Navigation(
    viewModel: SharedViewModel,
    navHost: NavHostController
) {
    NavHost(
        navController = navHost,
        startDestination = NavigationRoute.HomeScreen.route
    ) {
        composable(NavigationRoute.HomeScreen.route){
            TranslationPage(viewModel = viewModel)
        }
    }

}


sealed class NavigationRoute(val route: String) {
    object HomeScreen : NavigationRoute("home_screen")
}