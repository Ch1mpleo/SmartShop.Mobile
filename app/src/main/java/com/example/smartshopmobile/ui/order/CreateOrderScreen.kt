package com.example.smartshopmobile.ui.order

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartshopmobile.ui.components.SmartShopTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(
    cartItemIds: List<String>,
    onBackClick: () -> Unit,
    onOrderCreated: (String) -> Unit, // We'll still keep this for external logic if needed
    viewModel: OrderViewModel = hiltViewModel()
) {
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    val createOrderResult by viewModel.createOrderResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val paymentUrl by viewModel.paymentUrl.collectAsState()
    val paymentError by viewModel.paymentError.collectAsState()
    
    val context = LocalContext.current
    val TAG = "SmartShop_CreateOrder"

    // 1. When order is created successfully, automatically initiate payment
    LaunchedEffect(createOrderResult) {
        createOrderResult?.onSuccess { order ->
            Log.d(TAG, "Order created: ${order.id}. Initiating payment...")
            viewModel.initiatePayment(order.id)
            viewModel.resetCreateOrderResult()
        }
    }

    // 2. When payment URL is received, open the browser directly
    LaunchedEffect(paymentUrl) {
        paymentUrl?.let { url ->
            Log.d(TAG, "Opening Stripe URL: $url")
            try {
                val customTabsIntent = CustomTabsIntent.Builder().build()
                customTabsIntent.launchUrl(context, url.toUri())
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
            } finally {
                viewModel.clearPaymentUrl()
            }
        }
    }

    // 3. Handle errors
    LaunchedEffect(paymentError) {
        paymentError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearPaymentError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Shipping Information",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            SmartShopTextField(
                value = address,
                onValueChange = { address = it },
                label = "Billing Address",
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SmartShopTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { viewModel.createOrder(cartItemIds, address, phone) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = address.isNotBlank() && phone.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Confirm Order", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Preparing your secure payment...", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
