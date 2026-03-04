package com.example.smartshopmobile.data.model

data class AddToCartRequest(
    val productId: String,
    val quantity: Int
)

data class UpdateCartItemRequest(
    val quantity: Int
)
