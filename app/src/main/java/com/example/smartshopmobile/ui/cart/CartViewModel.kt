package com.example.smartshopmobile.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopmobile.data.api.CartService
import com.example.smartshopmobile.data.model.CartResponse
import com.example.smartshopmobile.data.model.UpdateCartItemRequest
import com.example.smartshopmobile.data.repository.GenericRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val genericRepository: GenericRepository,
    private val cartService: CartService
) : ViewModel() {

    private val _cart = MutableStateFlow<CartResponse?>(null)
    val cart: StateFlow<CartResponse?> = _cart.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchCart()
    }

    fun fetchCart() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            genericRepository.request { cartService.getMyCart() }.collect { result ->
                result.onSuccess { response ->
                    _cart.value = response.value?.data
                }.onFailure { e ->
                    _error.value = e.message
                }
                _isLoading.value = false
            }
        }
    }

    fun updateQuantity(cartItemId: String, quantity: Int) {
        if (quantity < 1) return
        viewModelScope.launch {
            genericRepository.request { 
                cartService.updateCartItemQuantity(cartItemId, UpdateCartItemRequest(quantity)) 
            }.collect { result ->
                result.onSuccess { response ->
                    _cart.value = response.value?.data
                }
            }
        }
    }

    fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            genericRepository.request { cartService.removeCartItem(cartItemId) }.collect { result ->
                result.onSuccess {
                    fetchCart() // Refresh
                }
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            genericRepository.request { cartService.clearCart() }.collect { result ->
                result.onSuccess {
                    _cart.value = null
                }
            }
        }
    }
}
