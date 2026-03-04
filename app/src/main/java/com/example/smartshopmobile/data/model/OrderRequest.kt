package com.example.smartshopmobile.data.model

data class CreateOrderRequest(
    val cartItemIds: List<String>,
    val billingAddress: String,
    val phoneNumber: String
)
