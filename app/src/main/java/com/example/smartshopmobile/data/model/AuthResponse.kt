package com.example.smartshopmobile.data.model

data class RegisterResponse(
    val isSuccess: Boolean,
    val value: RegisterValue?,
    val error: String?
)

data class RegisterValue(
    val code: String,
    val message: String,
    val data: UserData
)

data class UserData(
    val id: String,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val role: String,
    val createdAt: String
)

data class LoginResponse(
    val isSuccess: Boolean,
    val value: LoginValue?,
    val error: String?
)

data class LoginValue(
    val code: String,
    val message: String,
    val data: TokenData
)

data class TokenData(
    val accessToken: String,
    val refreshToken: String?
)