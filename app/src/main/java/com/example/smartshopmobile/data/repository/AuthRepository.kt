package com.example.smartshopmobile.data.repository

import com.example.smartshopmobile.data.api.AuthService
import com.example.smartshopmobile.data.local.TokenManager
import com.example.smartshopmobile.data.model.LoginRequest
import com.example.smartshopmobile.data.model.LoginResponse
import com.example.smartshopmobile.data.model.RegisterRequest
import com.example.smartshopmobile.data.model.RegisterResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) {
    suspend fun login(request: LoginRequest): Flow<Result<LoginResponse>> = flow {
        try {
            val response = authService.login(request)
            if (response.isSuccess && response.value?.data?.accessToken != null) {
                tokenManager.saveToken(response.value.data.accessToken)
                emit(Result.success(response))
            } else {
                emit(Result.failure(Exception(response.error ?: response.value?.message ?: "Login failed")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun register(request: RegisterRequest): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = authService.register(request)
            if (response.isSuccess) {
                emit(Result.success(response))
            } else {
                emit(Result.failure(Exception(response.error ?: response.value?.message ?: "Registration failed")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}