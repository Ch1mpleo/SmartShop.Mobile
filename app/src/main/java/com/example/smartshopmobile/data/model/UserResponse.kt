package com.example.smartshopmobile.data.model

data class UserResponse(
    val isSuccess: Boolean,
    val value: UserValue?,
    val error: String?
)

data class UserValue(
    val code: String,
    val message: String,
    val data: UserData
)