package com.example.smartshopmobile.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartshopmobile.ui.auth.LoginScreen
import com.example.smartshopmobile.ui.auth.RegisterScreen
import com.example.smartshopmobile.ui.home.WelcomeScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Welcome : Screen("welcome")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { _, _ -> navController.navigate(Screen.Welcome.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = { _, _, _, _ -> navController.navigate(Screen.Login.route) },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Welcome.route) {
            WelcomeScreen()
        }
    }
}