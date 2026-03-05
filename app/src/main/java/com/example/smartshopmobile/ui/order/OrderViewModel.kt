package com.example.smartshopmobile.ui.order

import android.util.Log
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

    private val TAG = "SmartShop_OrderVM"

    private val _orders = MutableStateFlow<List<OrderResponse>>(emptyList())
    val orders: StateFlow<List<OrderResponse>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _createOrderResult = MutableStateFlow<Result<OrderResponse>?>(null)
    val createOrderResult: StateFlow<Result<OrderResponse>?> = _createOrderResult.asStateFlow()

    private val _paymentUrl = MutableStateFlow<String?>(null)
    val paymentUrl: StateFlow<String?> = _paymentUrl.asStateFlow()

    private val _paymentError = MutableStateFlow<String?>(null)
    val paymentError: StateFlow<String?> = _paymentError.asStateFlow()

    private val _paymentStatus = MutableStateFlow<PaymentStatus>(PaymentStatus.Idle)
    val paymentStatus: StateFlow<PaymentStatus> = _paymentStatus.asStateFlow()

    sealed class PaymentStatus {
        object Idle : PaymentStatus()
        object Loading : PaymentStatus()
        object Verified : PaymentStatus()
        data class Error(val message: String) : PaymentStatus()
    }

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
                result.onSuccess {
                    Log.d(TAG, "Order created successfully: ${it.value?.data?.id}")
                }.onFailure {
                    Log.e(TAG, "Failed to create order", it)
                }
                _createOrderResult.value = result.map { it.value!!.data!! }
                _isLoading.value = false
            }
        }
    }

    fun initiatePayment(orderId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Initiating payment for order: $orderId")
            _isLoading.value = true
            _paymentError.value = null
            _paymentStatus.value = PaymentStatus.Loading
            genericRepository.request { checkoutService.initiatePayment(orderId) }.collect { result ->
                result.onSuccess { response ->
                    val url = response.value?.data
                    Log.d(TAG, "Payment URL received: $url")
                    if (!url.isNullOrBlank()) {
                        _paymentUrl.value = url
                    } else {
                        Log.e(TAG, "Payment URL is empty or null")
                        _paymentError.value = "Payment URL is empty"
                        _paymentStatus.value = PaymentStatus.Error("Payment URL is empty")
                    }
                }.onFailure { e ->
                    Log.e(TAG, "API call failed for initiatePayment", e)
                    _paymentError.value = e.message ?: "Failed to initiate payment"
                    _paymentStatus.value = PaymentStatus.Error(e.message ?: "Failed to initiate payment")
                }
                _isLoading.value = false
            }
        }
    }

    fun setPaymentVerified() {
        _paymentStatus.value = PaymentStatus.Verified
    }

    fun clearPaymentUrl() {
        Log.d(TAG, "Clearing payment URL")
        _paymentUrl.value = null
    }

    fun clearPaymentError() {
        _paymentError.value = null
        if (_paymentStatus.value is PaymentStatus.Error) {
            _paymentStatus.value = PaymentStatus.Idle
        }
    }

    fun resetCreateOrderResult() {
        _createOrderResult.value = null
    }

    fun resetPaymentStatus() {
        _paymentStatus.value = PaymentStatus.Idle
    }
}
