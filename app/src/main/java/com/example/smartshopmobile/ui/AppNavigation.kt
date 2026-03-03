package com.example.smartshopmobile.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smartshopmobile.ui.home.WelcomeScreen
import com.example.smartshopmobile.ui.product.ProductDetailScreen
import com.example.smartshopmobile.ui.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(
                onProfileClick = { navController.navigate("profile") },
                onProductClick = { productId -> navController.navigate("productDetail/$productId") }
            )
        }
        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "productDetail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) {
            ProductDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
