package com.example.smartshopmobile.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smartshopmobile.ui.auth.LoginScreen
import com.example.smartshopmobile.ui.auth.RegisterScreen
import com.example.smartshopmobile.ui.cart.CartScreen
import com.example.smartshopmobile.ui.checkout.CheckoutSuccessScreen
import com.example.smartshopmobile.ui.home.WelcomeScreen
import com.example.smartshopmobile.ui.order.CreateOrderScreen
import com.example.smartshopmobile.ui.order.MyOrdersScreen
import com.example.smartshopmobile.ui.product.ProductDetailScreen
import com.example.smartshopmobile.ui.profile.ProfileScreen
import com.example.smartshopmobile.ui.store.StoreLocationScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("login") {
            LoginScreen(
                onLoginClick = { _, _ ->
                    navController.navigate("welcome") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterClick = { _, _, _, _ ->
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("welcome") {
            WelcomeScreen(
                onProfileClick = { navController.navigate("profile") },
                onProductClick = { productId -> navController.navigate("productDetail/$productId") },
                onStoreClick = { navController.navigate("storeLocation") },
                onCartClick = { navController.navigate("cart") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onOrdersClick = { navController.navigate("myOrders") }
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
        composable("storeLocation") {
            StoreLocationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("cart") {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = { cartItemIds ->
                    val ids = cartItemIds.joinToString(",")
                    navController.navigate("createOrder/$ids")
                }
            )
        }
        composable(
            route = "createOrder/{cartItemIds}",
            arguments = listOf(navArgument("cartItemIds") { type = NavType.StringType })
        ) { backStackEntry ->
            val ids = backStackEntry.arguments?.getString("cartItemIds")?.split(",") ?: emptyList()
            CreateOrderScreen(
                cartItemIds = ids,
                onBackClick = { navController.popBackStack() },
                onOrderCreated = { _ ->
                    navController.navigate("checkoutSuccess") {
                        popUpTo("cart") { inclusive = true }
                    }
                }
            )
        }
        composable("myOrders") {
            MyOrdersScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("checkoutSuccess") {
            CheckoutSuccessScreen(
                onContinueShoppingClick = {
                    navController.navigate("welcome") {
                        popUpTo("checkoutSuccess") { inclusive = true }
                    }
                },
                onViewOrdersClick = {
                    navController.navigate("myOrders") {
                        popUpTo("checkoutSuccess") { inclusive = true }
                    }
                }
            )
        }
    }
}
