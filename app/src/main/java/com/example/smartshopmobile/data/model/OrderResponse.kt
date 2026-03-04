package com.example.smartshopmobile.data.model

data class OrderResponse(
    val id: String,
    val userId: String,
    val username: String,
    val paymentStatus: String,
    val paymentStatusText: String,
    val orderStatus: String,
    val orderStatusText: String,
    val billingAddress: String,
    val phoneNumber: String,
    val orderDate: String,
    val totalAmount: Double,
    val createdAt: String,
    val orderItems: List<OrderItemResponse>
)

data class OrderItemResponse(
    val id: String,
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val productPrice: Double,
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)
