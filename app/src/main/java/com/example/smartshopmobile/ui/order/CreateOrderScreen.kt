package com.example.smartshopmobile.ui.order

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartshopmobile.ui.components.SmartShopTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(
    cartItemIds: List<String>,
    onBackClick: () -> Unit,
    onOrderCreated: (String) -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    val createOrderResult by viewModel.createOrderResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(createOrderResult) {
        createOrderResult?.onSuccess { order ->
            onOrderCreated(order.id)
            viewModel.resetCreateOrderResult()
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
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Confirm Order", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
