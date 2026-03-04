package com.example.smartshopmobile.ui.checkout

import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartshopmobile.ui.order.OrderViewModel

@Composable
fun CheckoutSuccessScreen(
    orderId: String,
    onContinueShoppingClick: () -> Unit,
    onViewOrdersClick: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val TAG = "SmartShop_Checkout"
    val paymentUrl by viewModel.paymentUrl.collectAsState()
    val paymentError by viewModel.paymentError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    // Automatically initiate payment and redirect
    LaunchedEffect(orderId) {
        Log.d(TAG, "Screen loaded for orderId: $orderId. Initiating payment...")
        viewModel.initiatePayment(orderId)
    }

    LaunchedEffect(paymentUrl) {
        paymentUrl?.let { url ->
            Log.d(TAG, "Attempting to launch Custom Tab with URL: $url")
            try {
                val customTabsIntent = CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .build()
                customTabsIntent.launchUrl(context, url.toUri())
                Log.d(TAG, "Custom Tab launched successfully")
                viewModel.clearPaymentUrl()
            } catch (e: Exception) {
                Log.e(TAG, "Error launching Custom Tab", e)
                Toast.makeText(context, "Could not open browser: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (paymentError != null) {
            Log.w(TAG, "UI showing payment error: $paymentError")
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Payment Initialization Failed",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = paymentError ?: "",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { 
                Log.d(TAG, "User clicked Try Again")
                viewModel.initiatePayment(orderId) 
            }) {
                Text("Try Again")
            }
        } else {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color(0xFFE8F5E9)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.padding(20.dp).fillMaxSize(),
                    tint = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Order Placed!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your order #$orderId has been created. Redirecting to payment gateway...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { 
                        Log.d(TAG, "User clicked manual Pay with Stripe button")
                        viewModel.initiatePayment(orderId) 
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pay with Stripe", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onViewOrdersClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("View My Orders", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onContinueShoppingClick,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Continue Shopping", fontSize = 16.sp)
        }
    }
}
