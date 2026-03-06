package com.example.smartshopmobile.ui.management

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopmobile.data.api.CategoryService
import com.example.smartshopmobile.data.api.ProductService
import com.example.smartshopmobile.data.model.*
import com.example.smartshopmobile.data.repository.GenericRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagementViewModel @Inject constructor(
    private val genericRepository: GenericRepository,
    private val productService: ProductService,
    private val categoryService: CategoryService
) : ViewModel() {

    private val TAG = "SmartShop_ManagementVM"

    private val _products = MutableStateFlow<List<ProductResponse>>(emptyList())
    val products: StateFlow<List<ProductResponse>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryResponse>>(emptyList())
    val categories: StateFlow<List<CategoryResponse>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchProducts()
        fetchCategories()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            genericRepository.request { productService.getProducts(pageSize = 100) }.collect { result ->
                result.onSuccess { response ->
                    _products.value = response.value?.data?.items ?: emptyList()
                }.onFailure { e ->
                    Log.e(TAG, "Failed to fetch products", e)
                }
                _isLoading.value = false
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            genericRepository.request { categoryService.getAllCategories(pageSize = 100) }.collect { result ->
                result.onSuccess { response ->
                    _categories.value = response.value?.data?.items ?: emptyList()
                }.onFailure { e ->
                    Log.e(TAG, "Failed to fetch categories", e)
                }
                _isLoading.value = false
            }
        }
    }

    fun createCategory(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            genericRepository.request { categoryService.createCategory(CategoryRequest(name)) }.collect { result ->
                result.onSuccess { fetchCategories() }
                _isLoading.value = false
            }
        }
    }

    fun updateCategory(id: String, name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            genericRepository.request { categoryService.updateCategory(id, CategoryRequest(name)) }.collect { result ->
                result.onSuccess { fetchCategories() }
                _isLoading.value = false
            }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            genericRepository.request { categoryService.deleteCategory(id) }.collect { result ->
                result.onSuccess { fetchCategories() }
                _isLoading.value = false
            }
        }
    }

    fun createProduct(request: ProductRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            genericRepository.request { productService.createProduct(request) }.collect { result ->
                result.onSuccess { fetchProducts() }
                _isLoading.value = false
            }
        }
    }

    fun updateProduct(id: String, request: ProductRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            genericRepository.request { productService.updateProduct(id, request) }.collect { result ->
                result.onSuccess { fetchProducts() }
                _isLoading.value = false
            }
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            genericRepository.request { productService.deleteProduct(id) }.collect { result ->
                result.onSuccess { fetchProducts() }
                _isLoading.value = false
            }
        }
    }
}
