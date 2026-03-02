package com.example.smartshopmobile.data.api

import com.example.smartshopmobile.data.model.ApiResponse
import com.example.smartshopmobile.data.model.UserData
import retrofit2.http.GET

interface UserService {
    @GET("api/users/me")
    suspend fun getCurrentUser(): ApiResponse<UserData>
}