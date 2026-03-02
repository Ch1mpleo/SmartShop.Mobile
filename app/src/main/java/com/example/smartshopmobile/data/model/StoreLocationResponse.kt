package com.example.smartshopmobile.data.model

data class StoreLocationResponse(
    val id: String,
    val storeName: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val createdAt: String
)