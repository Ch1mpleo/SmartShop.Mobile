package com.example.smartshopmobile.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopmobile.data.api.CheckoutService
import com.example.smartshopmobile.data.api.OrderService
import com.example.smartshopmobile.data.model.CreateOrderRequest
import com.example.smartshopmobile.data.model.OrderResponse
import com.example.smartshopmobile.data.repository.GenericRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val genericRepository: GenericRepository,
    private val orderService: OrderService,
    private val checkoutService: CheckoutService
) : ViewModel() {

    private val _orders = MutableStateFlow<List<OrderResponse>>(emptyList())
    val orders: StateFlow<List<OrderResponse>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _createOrderResult = MutableStateFlow<Result<OrderResponse>?>(null)
    val createOrderResult: StateFlow<Result<OrderResponse>?> = _createOrderResult.asStateFlow()

    private val _paymentUrl = MutableStateFlow<String?>(null)
    val paymentUrl: StateFlow<String?> = _paymentUrl.asStateFlow()

    fun fetchMyOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            genericRepository.request { orderService.getMyOrders() }.collect { result ->
                result.onSuccess { response ->
                    _orders.value = response.value?.data?.items ?: emptyList()
                }
                _isLoading.value = false
            }
        }
    }

    fun createOrder(cartItemIds: List<String>, address: String, phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val request = CreateOrderRequest(cartItemIds, address, phone)
            genericRepository.request { orderService.createOrder(request) }.collect { result ->
                _createOrderResult.value = result.map { it.value!!.data!! }
                _isLoading.value = false
            }
        }
    }

    fun initiatePayment(orderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            genericRepository.request { checkoutService.initiatePayment(orderId) }.collect { result ->
                result.onSuccess { response ->
                    _paymentUrl.value = response.value?.data
                }
                _isLoading.value = false
            }
        }
    }

    fun clearPaymentUrl() {
        _paymentUrl.value = null
    }

    fun resetCreateOrderResult() {
        _createOrderResult.value = null
    }
}
