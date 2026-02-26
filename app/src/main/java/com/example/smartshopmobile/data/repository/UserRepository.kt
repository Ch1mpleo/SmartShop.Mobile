package com.example.smartshopmobile.data.repository

import com.example.smartshopmobile.data.api.UserService
import com.example.smartshopmobile.data.model.UserResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService
) : BaseRepository() {

    suspend fun getCurrentUser(): Flow<Result<UserResponse>> = safeApiCall {
        val response = userService.getCurrentUser()
        if (response.isSuccess) {
            response
        } else {
            throw Exception(response.error ?: response.value?.message ?: "Failed to fetch user profile")
        }
    }
}