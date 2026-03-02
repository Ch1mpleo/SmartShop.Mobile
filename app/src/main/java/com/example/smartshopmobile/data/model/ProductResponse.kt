package com.example.smartshopmobile.data.model

data class ProductResponse(
    val id: String,
    val productName: String,
    val briefDescription: String,
    val fullDescription: String,
    val technicalSpecifications: String,
    val price: Double,
    val imageUrl: String,
    val status: String,
    val availableQuantity: Int,
    val categoryId: String,
    val categoryName: String,
    val createdAt: String
)