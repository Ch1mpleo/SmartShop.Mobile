package com.example.smartshopmobile.data.repository

import com.example.smartshopmobile.data.api.AuthService
import com.example.smartshopmobile.data.local.TokenManager
import com.example.smartshopmobile.data.model.LoginRequest
import com.example.smartshopmobile.data.model.LoginResponse
import com.example.smartshopmobile.data.model.RegisterRequest
import com.example.smartshopmobile.data.model.RegisterResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) : BaseRepository() {

    suspend fun login(request: LoginRequest): Flow<Result<LoginResponse>> = safeApiCall {
        val response = authService.login(request)
        if (response.isSuccess && response.value?.data?.accessToken != null) {
            tokenManager.saveToken(response.value.data.accessToken)
            response
        } else {
            throw Exception(response.error ?: response.value?.message ?: "Login failed")
        }
    }

    suspend fun register(request: RegisterRequest): Flow<Result<RegisterResponse>> = safeApiCall {
        val response = authService.register(request)
        if (response.isSuccess) {
            response
        } else {
            throw Exception(response.error ?: response.value?.message ?: "Registration failed")
        }
    }
}