package com.example.smartshopmobile.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopmobile.data.api.CartService
import com.example.smartshopmobile.data.api.CategoryService
import com.example.smartshopmobile.data.api.ProductService
import com.example.smartshopmobile.data.api.UserService
import com.example.smartshopmobile.data.local.TokenManager
import com.example.smartshopmobile.data.model.CategoryResponse
import com.example.smartshopmobile.data.model.ProductResponse
import com.example.smartshopmobile.data.model.UserData
import com.example.smartshopmobile.data.repository.GenericRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val genericRepository: GenericRepository,
    private val categoryService: CategoryService,
    private val productService: ProductService,
    private val userService: UserService,
    private val cartService: CartService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryResponse>>(emptyList())
    val categories: StateFlow<List<CategoryResponse>> = _categories.asStateFlow()

    private val _products = MutableStateFlow<List<ProductResponse>>(emptyList())
    val products: StateFlow<List<ProductResponse>> = _products.asStateFlow()

    private val _user = MutableStateFlow<UserData?>(null)
    val user: StateFlow<UserData?> = _user.asStateFlow()

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _brand = MutableStateFlow<String?>(null)
    val brand: StateFlow<String?> = _brand.asStateFlow()

    private val _minPrice = MutableStateFlow<Double?>(null)
    val minPrice: StateFlow<Double?> = _minPrice.asStateFlow()

    private val _maxPrice = MutableStateFlow<Double?>(null)
    val maxPrice: StateFlow<Double?> = _maxPrice.asStateFlow()

    private val _minRating = MutableStateFlow<Double?>(null)
    val minRating: StateFlow<Double?> = _minRating.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val userJob = launch { fetchUser() }
                val categoriesJob = launch { fetchCategories() }
                val productsJob = launch { fetchProducts() }
                val cartJob = launch { fetchCartCount() }
                joinAll(userJob, categoriesJob, productsJob, cartJob)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        tokenManager.clearToken()
        onLogoutSuccess()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            fetchProducts()
        }
    }

    fun onCategorySelected(categoryId: String?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
        fetchProducts()
    }

    fun onBrandChanged(brand: String?) {
        _brand.value = brand
        fetchProducts()
    }

    fun onPriceRangeChanged(min: Double?, max: Double?) {
        _minPrice.value = min
        _maxPrice.value = max
        fetchProducts()
    }

    fun onMinRatingChanged(rating: Double?) {
        _minRating.value = rating
        fetchProducts()
    }

    private suspend fun fetchUser() {
        genericRepository.request { userService.getCurrentUser() }.collect { result ->
            result.onSuccess { response ->
                _user.value = response.value?.data
            }.onFailure {
                // Handle user fetch error silently
            }
        }
    }

    private suspend fun fetchCategories() {
        genericRepository.request { categoryService.getAllCategories() }.collect { result ->
            result.onSuccess { response ->
                _categories.value = response.value?.data?.items ?: emptyList()
            }.onFailure { e ->
                _error.value = e.message
            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            genericRepository.request { 
                productService.getProducts(
                    search = _searchQuery.value.takeIf { it.isNotBlank() },
                    categoryId = _selectedCategoryId.value,
                    brand = _brand.value,
                    minPrice = _minPrice.value,
                    maxPrice = _maxPrice.value,
                    minRating = _minRating.value
                ) 
            }.collect { result ->
                result.onSuccess { response ->
                    _products.value = response.value?.data?.items ?: emptyList()
                }.onFailure { e ->
                    _error.value = e.message
                }
            }
        }
    }

    fun fetchCartCount() {
        viewModelScope.launch {
            genericRepository.request { cartService.getMyCart() }.collect { result ->
                result.onSuccess { response ->
                    _cartItemCount.value = response.value?.data?.totalItems ?: 0
                }.onFailure {
                    // Handle silently
                }
            }
        }
    }
}
