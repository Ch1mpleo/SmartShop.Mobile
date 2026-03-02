package com.example.smartshopmobile.data.model

data class ProductStorageResponse(
    val id: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val lastStockedBy: String?,
    val lastStockedAt: String?,
    val createdAt: String,
    val updatedAt: String?
)