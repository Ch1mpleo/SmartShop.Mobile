package com.example.smartshopmobile.ui.store

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartshopmobile.data.model.StoreLocationResponse
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreLocationScreen(
    onBackClick: () -> Unit,
    viewModel: StoreLocationViewModel = hiltViewModel()
) {
    val stores by viewModel.stores.collectAsState()
    val nearestStores by viewModel.nearestStores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val context = LocalContext.current

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Store Locator") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp, max = 450.dp)
            ) {
                Text(
                    text = if (nearestStores.isEmpty()) "Find Nearest Stores" else "Nearest Stores Found",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                
                if (nearestStores.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tap on the map above to select a location",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(nearestStores) { store ->
                            StoreItem(store)
                        }
                    }
                }
            }
        },
        sheetPeekHeight = 80.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetShadowElevation = 16.dp
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Map Layer
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(6.0)
                        controller.setCenter(GeoPoint(10.7756, 106.7019)) // HCM City

                        val receiver = object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                viewModel.findNearestStores(p.latitude, p.longitude)
                                return true
                            }
                            override fun longPressHelper(p: GeoPoint): Boolean = false
                        }
                        overlays.add(MapEventsOverlay(receiver))
                    }
                },
                update = { view ->
                    view.overlays.removeIf { it is Marker }
                    
                    // Store Markers
                    stores.forEach { store ->
                        val marker = Marker(view)
                        marker.position = GeoPoint(store.latitude, store.longitude)
                        marker.title = store.storeName
                        marker.snippet = store.address
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        // Standard store icon
                        marker.icon = context.getDrawable(android.R.drawable.ic_menu_directions)
                        view.overlays.add(marker)
                    }

                    // User Selection Marker (Different Icon)
                    userLocation?.let {
                        val userMarker = Marker(view)
                        userMarker.position = GeoPoint(it.latitude, it.longitude)
                        userMarker.title = "Your Selection"
                        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        // Distinctive user icon
                        userMarker.icon = context.getDrawable(android.R.drawable.ic_menu_myplaces)
                        view.overlays.add(userMarker)
                    }
                    
                    view.invalidate()
                }
            )

            // UI Layer: Floating Guide Card
            AnimatedVisibility(
                visible = userLocation == null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Tap map to find nearest stores",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun StoreItem(store: StoreLocationResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        ListItem(
            headlineContent = { 
                Text(
                    text = store.storeName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                ) 
            },
            supportingContent = { 
                Text(
                    text = store.address,
                    style = MaterialTheme.typography.bodySmall
                ) 
            },
            leadingContent = {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}
