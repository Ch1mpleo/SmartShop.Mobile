package com.example.smartshopmobile.data.model

data class ProductRequest(
    val productName: String,
    val briefDescription: String,
    val fullDescription: String,
    val technicalSpecifications: String,
    val price: Long,
    val imageUrl: String,
    val categoryId: String,
    val status: String? = null,
    val availableQuantity: Int? = null,
    val categoryName: String? = null
)
