package com.example.smartshopmobile.data.api

import com.example.smartshopmobile.data.model.LoginRequest
import com.example.smartshopmobile.data.model.LoginResponse
import com.example.smartshopmobile.data.model.RegisterRequest
import com.example.smartshopmobile.data.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}