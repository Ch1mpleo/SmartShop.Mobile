package com.example.smartshopmobile.ui.store

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import org.osmdroid.views.overlay.Polyline
import java.util.Locale
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreLocationScreen(
    onBackClick: () -> Unit,
    viewModel: StoreLocationViewModel = hiltViewModel()
) {
    val stores by viewModel.stores.collectAsState()
    val nearestStores by viewModel.nearestStores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRouting by viewModel.isRouting.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val roadPoints by viewModel.roadPoints.collectAsState()
    val context = LocalContext.current

    var selectedStoreForRoute by remember { mutableStateOf<StoreLocationResponse?>(null) }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    // Automatically select the nearest store for routing when results come in
    LaunchedEffect(nearestStores) {
        if (nearestStores.isNotEmpty()) {
            if (selectedStoreForRoute == null || !nearestStores.any { it.id == selectedStoreForRoute?.id }) {
                selectedStoreForRoute = nearestStores.first()
            }
        }
    }

    // Fetch road route whenever user location or selected store changes
    LaunchedEffect(userLocation, selectedStoreForRoute) {
        val uLoc = userLocation
        val sStore = selectedStoreForRoute
        if (uLoc != null && sStore != null) {
            viewModel.fetchRoadRoute(
                uLoc.latitude, uLoc.longitude,
                sStore.latitude, sStore.longitude
            )
        } else {
            viewModel.clearRoute()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Store Locator", fontWeight = FontWeight.Bold) },
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
                    text = if (nearestStores.isEmpty()) "Find Nearest Stores" else "Nearest SmartShops",
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
                            text = "Tap on the map above to select your location",
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
                            StoreItem(
                                store = store,
                                isSelected = selectedStoreForRoute?.id == store.id,
                                userLocation = userLocation,
                                onSelect = { selectedStoreForRoute = store }
                            )
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
                        controller.setZoom(12.0)
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
                    view.overlays.removeIf { it is Marker || it is Polyline }
                    
                    // Draw Road Route
                    if (roadPoints.isNotEmpty()) {
                        val line = Polyline(view)
                        line.outlinePaint.color = android.graphics.Color.parseColor("#6750A4")
                        line.outlinePaint.strokeWidth = 12f
                        line.setPoints(roadPoints)
                        view.overlays.add(line)
                    }

                    // Store Markers
                    stores.forEach { store ->
                        val marker = Marker(view)
                        marker.position = GeoPoint(store.latitude, store.longitude)
                        marker.title = store.storeName
                        marker.snippet = store.address
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.icon = context.getDrawable(android.R.drawable.ic_menu_directions)
                        
                        if (selectedStoreForRoute?.id == store.id) {
                            marker.showInfoWindow()
                        }
                        
                        view.overlays.add(marker)
                    }

                    // User Selection Marker
                    userLocation?.let {
                        val userMarker = Marker(view)
                        userMarker.position = GeoPoint(it.latitude, it.longitude)
                        userMarker.title = "Starting Point"
                        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        userMarker.icon = context.getDrawable(android.R.drawable.ic_menu_myplaces)
                        view.overlays.add(userMarker)
                    }
                    
                    view.invalidate()
                }
            )

            // Guidance UI
            if (userLocation == null) {
                Surface(
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MyLocation, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Tap map to set your location", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Full-screen loading overlay
            if (isLoading || isRouting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isRouting) "Calculating road route..." else "Finding stores...",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoreItem(
    store: StoreLocationResponse,
    isSelected: Boolean,
    userLocation: UserLocation?,
    onSelect: () -> Unit
) {
    val distance = if (userLocation != null) {
        calculateDistance(userLocation.latitude, userLocation.longitude, store.latitude, store.longitude)
    } else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
            else 
                MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
    ) {
        ListItem(
            headlineContent = { 
                Text(
                    text = store.storeName, 
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                ) 
            },
            supportingContent = { 
                Column {
                    Text(
                        text = store.address, 
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (distance != null) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun, 
                                contentDescription = null, 
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${String.format(Locale.getDefault(), "%.2f", distance)} km",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            },
            leadingContent = {
                Surface(
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Navigation else Icons.Default.Storefront,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            },
            trailingContent = {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle, 
                        contentDescription = "Selected", 
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371 // Earth radius in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}
