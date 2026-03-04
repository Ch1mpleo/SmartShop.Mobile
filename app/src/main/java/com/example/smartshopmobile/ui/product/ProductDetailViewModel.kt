package com.example.smartshopmobile.ui.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopmobile.data.api.CartService
import com.example.smartshopmobile.data.api.ProductService
import com.example.smartshopmobile.data.model.AddToCartRequest
import com.example.smartshopmobile.data.model.ProductResponse
import com.example.smartshopmobile.data.repository.GenericRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val genericRepository: GenericRepository,
    private val productService: ProductService,
    private val cartService: CartService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = checkNotNull(savedStateHandle["productId"])

    private val _product = MutableStateFlow<ProductResponse?>(null)
    val product: StateFlow<ProductResponse?> = _product.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isAddingToCart = MutableStateFlow(false)
    val isAddingToCart: StateFlow<Boolean> = _isAddingToCart.asStateFlow()

    private val _cartMessage = MutableStateFlow<String?>(null)
    val cartMessage: StateFlow<String?> = _cartMessage.asStateFlow()

    init {
        fetchProductDetails()
    }

    fun fetchProductDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            genericRepository.request { productService.getProductById(productId) }.collect { result ->
                result.onSuccess { response ->
                    _product.value = response.value?.data
                }.onFailure { e ->
                    _error.value = e.message
                }
                _isLoading.value = false
            }
        }
    }

    fun addToCart(quantity: Int) {
        viewModelScope.launch {
            _isAddingToCart.value = true
            _cartMessage.value = null
            genericRepository.request { 
                cartService.addToCart(AddToCartRequest(productId, quantity)) 
            }.collect { result ->
                result.onSuccess {
                    _cartMessage.value = "Added to cart successfully!"
                }.onFailure { e ->
                    _cartMessage.value = e.message ?: "Failed to add to cart"
                }
                _isAddingToCart.value = false
            }
        }
    }

    fun clearCartMessage() {
        _cartMessage.value = null
    }
}