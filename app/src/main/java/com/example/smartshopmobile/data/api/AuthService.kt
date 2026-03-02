package com.example.smartshopmobile.data.api

import com.example.smartshopmobile.data.model.ApiResponse
import com.example.smartshopmobile.data.model.LoginRequest
import com.example.smartshopmobile.data.model.RegisterRequest
import com.example.smartshopmobile.data.model.TokenData
import com.example.smartshopmobile.data.model.UserData
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<UserData>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<TokenData>
}