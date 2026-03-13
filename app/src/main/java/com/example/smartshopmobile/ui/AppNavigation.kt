package com.example.smartshopmobile.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.smartshopmobile.ui.auth.AuthState
import com.example.smartshopmobile.ui.auth.AuthViewModel
import com.example.smartshopmobile.ui.auth.LoginScreen
import com.example.smartshopmobile.ui.auth.RegisterScreen
import com.example.smartshopmobile.ui.cart.CartScreen
import com.example.smartshopmobile.ui.checkout.CheckoutSuccessScreen
import com.example.smartshopmobile.ui.home.WelcomeScreen
import com.example.smartshopmobile.ui.management.ManagementScreen
import com.example.smartshopmobile.ui.order.CreateOrderScreen
import com.example.smartshopmobile.ui.order.MyOrdersScreen
import com.example.smartshopmobile.ui.product.ProductDetailScreen
import com.example.smartshopmobile.ui.profile.ProfileScreen
import com.example.smartshopmobile.ui.store.StoreLocationScreen

@Composable
fun AppNavigation(
    onNavControllerReady: (NavController) -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()
    val TAG = "SmartShop_Navigation"
    
    val ROLE_ADMIN = "Admin"
    val ROLE_STAFF = "Staff"

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(navController) {
        onNavControllerReady(navController)
    }

    // Centralized Auth Navigation logic
    LaunchedEffect(currentUser, currentRoute) {
        val user = currentUser
        if (user != null) {
            val destination = if (user.role.equals(ROLE_ADMIN, ignoreCase = true) || 
                                 user.role.equals(ROLE_STAFF, ignoreCase = true)) {
                "management"
            } else {
                "welcome"
            }
            
            if (currentRoute == "login" || currentRoute == "register") {
                Log.d(TAG, "Redirecting to $destination")
                navController.navigate(destination) {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }
        } else if (loginState !is AuthState.Checking) {
            if (currentRoute != null && currentRoute != "login" && currentRoute != "register") {
                Log.d(TAG, "Redirecting to login")
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    // Show loading screen during initial auth check
    if (loginState is AuthState.Checking) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    } else {
        NavHost(
            navController = navController, 
            startDestination = if (currentUser != null) {
                if (currentUser?.role.equals(ROLE_ADMIN, ignoreCase = true) || 
                    currentUser?.role.equals(ROLE_STAFF, ignoreCase = true)) "management" else "welcome"
            } else "login"
        ) {
            composable("login") {
                LoginScreen(
                    onLoginClick = { _, _ -> },
                    onNavigateToRegister = { navController.navigate("register") },
                    viewModel = authViewModel
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
                    onLogout = { authViewModel.logout() }
                )
            }
            composable("management") {
                ManagementScreen(onLogout = { authViewModel.logout() })
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
                ProductDetailScreen(onBackClick = { navController.popBackStack() })
            }
            composable("storeLocation") {
                StoreLocationScreen(onBackClick = { navController.popBackStack() })
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
                    onOrderCreated = { }
                )
            }
            composable("myOrders") {
                MyOrdersScreen(onBackClick = { navController.popBackStack() })
            }
            composable(
                route = "checkoutSuccess/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType }),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "smartshop://payment/success?orderId={orderId}" }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                CheckoutSuccessScreen(
                    orderId = orderId,
                    onContinueShoppingClick = {
                        navController.navigate("welcome") {
                            popUpTo("checkoutSuccess/$orderId") { inclusive = true }
                        }
                    },
                    onViewOrdersClick = {
                        navController.navigate("myOrders") {
                            popUpTo("checkoutSuccess/$orderId") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
