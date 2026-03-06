package com.example.smartshopmobile.ui.management

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        ManagementTabItem("Categories", Icons.Default.Category),
        ManagementTabItem("Products", Icons.Default.ShoppingBag),
        ManagementTabItem("Stores", Icons.Default.LocationOn)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Management") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tab.title) },
                        icon = { Icon(tab.icon, contentDescription = null) }
                    )
                }
            }
            
            when (selectedTab) {
                0 -> CategoryManagementTab()
                1 -> ProductManagementTab()
                2 -> StoreManagementTab()
            }
        }
    }
}

data class ManagementTabItem(val title: String, val icon: ImageVector)

@Composable
fun CategoryManagementTab() {
    // Placeholder for now
    Text("Category Management Content", modifier = Modifier.fillMaxSize())
}

@Composable
fun ProductManagementTab() {
    // Placeholder for now
    Text("Product Management Content", modifier = Modifier.fillMaxSize())
}

@Composable
fun StoreManagementTab() {
    // Placeholder for now
    Text("Store Management Content", modifier = Modifier.fillMaxSize())
}
