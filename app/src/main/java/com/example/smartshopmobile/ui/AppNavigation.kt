package com.example.smartshopmobile.ui

import android.util.Log
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
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
    val TAG = "SmartShop_Navigation"
    
    // Roles constants
    val ROLE_ADMIN = "Admin"
    val ROLE_STAFF = "Staff"
    val ROLE_CUSTOMER = "Customer"

    // Observe current destination
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(navController) {
        onNavControllerReady(navController)
    }

    // Robust Centralized Auth Navigation logic
    LaunchedEffect(currentUser, currentRoute) {
        val user = currentUser
        Log.d(TAG, "Auth Effect Triggered: user=${user?.username}, role=${user?.role}, currentRoute=$currentRoute")
        
        if (user != null) {
            val destination = if (user.role.equals(ROLE_ADMIN, ignoreCase = true) || 
                                 user.role.equals(ROLE_STAFF, ignoreCase = true)) {
                "management"
            } else {
                "welcome"
            }
            
            // If we are on login or register, we MUST navigate to the main app
            if (currentRoute == "login" || currentRoute == "register" || currentRoute == null) {
                Log.d(TAG, "User is logged in. Redirecting to $destination from $currentRoute")
                navController.navigate(destination) {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }
        } else {
            // User is null (Logout or No Token). Redirect to login if we are in the main app
            if (currentRoute != null && currentRoute != "login" && currentRoute != "register") {
                Log.d(TAG, "No user session. Redirecting to login from $currentRoute")
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController, 
        startDestination = "login" // Start destination is always login; LaunchedEffect handles the redirect if already logged in
    ) {
        composable("login") {
            LoginScreen(
                onLoginClick = { _, _ -> 
                    Log.d(TAG, "Login reported success - Navigation handled by Auth Effect")
                },
                onNavigateToRegister = { navController.navigate("register") },
                viewModel = authViewModel // SHARED VIEWMODEL INSTANCE
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
                    authViewModel.logout()
                }
            )
        }
        composable("management") {
            ManagementScreen(
                onLogout = {
                    authViewModel.logout()
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
                onOrderCreated = { /* Redirection handled inside CreateOrderScreen */ }
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
