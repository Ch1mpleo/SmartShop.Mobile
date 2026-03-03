package com.example.smartshopmobile.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopmobile.data.api.CategoryService
import com.example.smartshopmobile.data.api.ProductService
import com.example.smartshopmobile.data.api.UserService
import com.example.smartshopmobile.data.model.CategoryResponse
import com.example.smartshopmobile.data.model.ProductResponse
import com.example.smartshopmobile.data.model.UserData
import com.example.smartshopmobile.data.repository.GenericRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val genericRepository: GenericRepository,
    private val categoryService: CategoryService,
    private val productService: ProductService,
    private val userService: UserService
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryResponse>>(emptyList())
    val categories: StateFlow<List<CategoryResponse>> = _categories.asStateFlow()

    private val _products = MutableStateFlow<List<ProductResponse>>(emptyList())
    val products: StateFlow<List<ProductResponse>> = _products.asStateFlow()

    private val _user = MutableStateFlow<UserData?>(null)
    val user: StateFlow<UserData?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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
                joinAll(userJob, categoriesJob, productsJob)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchUser() {
        genericRepository.request { userService.getCurrentUser() }.collect { result ->
            result.onSuccess { response ->
                _user.value = response.value?.data
            }.onFailure {
                // Handle user fetch error silently or show specific error
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

    private suspend fun fetchProducts() {
        genericRepository.request { productService.getProducts() }.collect { result ->
            result.onSuccess { response ->
                _products.value = response.value?.data?.items ?: emptyList()
            }.onFailure { e ->
                _error.value = e.message
            }
        }
    }
}