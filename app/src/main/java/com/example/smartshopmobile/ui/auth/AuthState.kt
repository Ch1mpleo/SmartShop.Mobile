package com.example.smartshopmobile.ui.auth

import com.example.smartshopmobile.data.model.UserData

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Checking : AuthState()
    data class Success(val user: UserData? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}
