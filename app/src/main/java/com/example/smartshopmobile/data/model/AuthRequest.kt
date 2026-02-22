package com.example.smartshopmobile.data.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String,
    val gender: Boolean
)

data class LoginRequest(
    val email: String,
    val password: String
)