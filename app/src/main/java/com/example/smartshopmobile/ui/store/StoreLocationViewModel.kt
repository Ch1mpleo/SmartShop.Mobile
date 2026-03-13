package com.example.smartshopmobile.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopmobile.data.api.OsrmService
import com.example.smartshopmobile.data.api.StoreLocationService
import com.example.smartshopmobile.data.model.StoreLocationResponse
import com.example.smartshopmobile.data.repository.GenericRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

data class UserLocation(val latitude: Double, val longitude: Double)

@HiltViewModel
class StoreLocationViewModel @Inject constructor(
    private val genericRepository: GenericRepository,
    private val storeLocationService: StoreLocationService,
    private val osrmService: OsrmService
) : ViewModel() {

    private val _stores = MutableStateFlow<List<StoreLocationResponse>>(emptyList())
    val stores: StateFlow<List<StoreLocationResponse>> = _stores.asStateFlow()

    private val _nearestStores = MutableStateFlow<List<StoreLocationResponse>>(emptyList())
    val nearestStores: StateFlow<List<StoreLocationResponse>> = _nearestStores.asStateFlow()

    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation.asStateFlow()

    private val _roadPoints = MutableStateFlow<List<GeoPoint>>(emptyList())
    val roadPoints: StateFlow<List<GeoPoint>> = _roadPoints.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchAllStores()
    }

    fun fetchAllStores() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            genericRepository.request { storeLocationService.getAllStoreLocations() }.collect { result ->
                result.onSuccess { response ->
                    _stores.value = response.value?.data?.items ?: emptyList()
                }.onFailure { e ->
                    _error.value = e.message
                }
                _isLoading.value = false
            }
        }
    }

    fun findNearestStores(latitude: Double, longitude: Double) {
        _userLocation.value = UserLocation(latitude, longitude)
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            genericRepository.request { 
                storeLocationService.getNearestStoreLocations(latitude, longitude)
            }.collect { result ->
                result.onSuccess { response ->
                    _nearestStores.value = response.value?.data ?: emptyList()
                }.onFailure { e ->
                    _error.value = e.message
                }
                _isLoading.value = false
            }
        }
    }

    fun fetchRoadRoute(startLat: Double, startLon: Double, endLat: Double, endLon: Double) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val coordinates = "$startLon,$startLat;$endLon,$endLat"
                val response = osrmService.getRoute(coordinates)
                
                if (response.routes.isNotEmpty()) {
                    val points = response.routes[0].geometry.coordinates.map { 
                        GeoPoint(it[1], it[0]) // [lon, lat] -> GeoPoint(lat, lon)
                    }
                    _roadPoints.value = points
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch road route: ${e.message}"
                _roadPoints.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearRoute() {
        _roadPoints.value = emptyList()
    }
}
