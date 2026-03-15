package com.example.smartshopmobile.ui.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartshopmobile.data.model.CategoryResponse
import com.example.smartshopmobile.data.model.ProductRequest
import com.example.smartshopmobile.data.model.ProductResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
    onLogout: () -> Unit,
    onChatClick: () -> Unit,
    viewModel: ManagementViewModel = hiltViewModel()
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
                title = { Text("Management Portal", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onChatClick,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 80.dp) // Offset to avoid overlapping with tab FABs
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat Support")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tab.title, style = MaterialTheme.typography.labelLarge) },
                        icon = { Icon(tab.icon, contentDescription = null) }
                    )
                }
            }
            
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> CategoryManagementTab(viewModel)
                    1 -> ProductManagementTab(viewModel)
                    2 -> StoreManagementTab()
                }
                
                val isLoading by viewModel.isLoading.collectAsState()
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

data class ManagementTabItem(val title: String, val icon: ImageVector)

@Composable
fun CategoryManagementTab(viewModel: ManagementViewModel) {
    val categories by viewModel.categories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<CategoryResponse?>(null) }
    var categoryName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(category.categoryName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("${category.productCount} Products", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { 
                            categoryToEdit = category
                            categoryName = category.categoryName
                            showAddDialog = true 
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { viewModel.deleteCategory(category.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { 
                categoryToEdit = null
                categoryName = ""
                showAddDialog = true 
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Category")
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (categoryToEdit == null) "Add Category" else "Edit Category") },
            text = {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (categoryToEdit == null) viewModel.createCategory(categoryName)
                    else viewModel.updateCategory(categoryToEdit!!.id, categoryName)
                    showAddDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ProductManagementTab(viewModel: ManagementViewModel) {
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showProductDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ProductResponse?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.productName, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(product.categoryName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            Text("${product.price.toInt()} VND", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.secondary)
                            Text("Stock: ${product.availableQuantity}", style = MaterialTheme.typography.labelSmall)
                        }
                        Column {
                            IconButton(onClick = { 
                                productToEdit = product
                                showProductDialog = true 
                            }) {
                                Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.deleteProduct(product.id) }) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { 
                productToEdit = null
                showProductDialog = true 
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Product")
        }
    }

    if (showProductDialog) {
        ProductDialog(
            product = productToEdit,
            categories = categories,
            onDismiss = { showProductDialog = false },
            onSave = { request ->
                if (productToEdit == null) viewModel.createProduct(request)
                else viewModel.updateProduct(productToEdit!!.id, request)
                showProductDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDialog(
    product: ProductResponse?,
    categories: List<CategoryResponse>,
    onDismiss: () -> Unit,
    onSave: (ProductRequest) -> Unit
) {
    var name by remember { mutableStateOf(product?.productName ?: "") }
    var briefDesc by remember { mutableStateOf(product?.briefDescription ?: "") }
    var fullDesc by remember { mutableStateOf(product?.fullDescription ?: "") }
    var specs by remember { mutableStateOf(product?.technicalSpecifications ?: "") }
    var price by remember { mutableStateOf(product?.price?.toLong()?.toString() ?: "") }
    var imageUrl by remember { mutableStateOf(product?.imageUrl ?: "") }
    var availableQuantity by remember { mutableStateOf(product?.availableQuantity?.toString() ?: "0") }
    var status by remember { mutableStateOf(product?.status ?: "InStock") }
    
    var selectedCategory by remember { mutableStateOf(categories.find { it.id == product?.categoryId } ?: categories.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Create Product" else "Update Product", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory?.categoryName ?: "Select Category",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.categoryName) },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Price (VND)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = availableQuantity,
                            onValueChange = { availableQuantity = it },
                            label = { Text("Quantity") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = briefDesc,
                        onValueChange = { briefDesc = it },
                        label = { Text("Brief Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 2
                    )
                }

                item {
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Image URL") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = fullDesc,
                        onValueChange = { fullDesc = it },
                        label = { Text("Full Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3
                    )
                }

                item {
                    OutlinedTextField(
                        value = specs,
                        onValueChange = { specs = it },
                        label = { Text("Specifications") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        ProductRequest(
                            categoryId = selectedCategory?.id ?: "",
                            productName = name,
                            briefDescription = briefDesc,
                            fullDescription = fullDesc,
                            technicalSpecifications = specs,
                            price = price.toLongOrNull() ?: 0L,
                            imageUrl = imageUrl,
                            availableQuantity = availableQuantity.toIntOrNull() ?: 0,
                            status = status
                        )
                    )
                },
                enabled = name.isNotBlank() && price.isNotBlank() && selectedCategory != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun StoreManagementTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Store Management coming soon...")
    }
}
