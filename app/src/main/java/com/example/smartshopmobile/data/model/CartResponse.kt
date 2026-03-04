package com.example.smartshopmobile.data.model

data class CartResponse(
    val id: String,
    val userId: String,
    val username: String,
    val totalPrice: Double,
    val status: String,
    val statusText: String,
    val totalItems: Int,
    val createdAt: String,
    val cartItems: List<CartItemResponse>
)

data class CartItemResponse(
    val id: String,
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val productPrice: Double,
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)
