package com.example.smartshopmobile.data.repository

import com.example.smartshopmobile.data.model.ApiResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenericRepository @Inject constructor() : BaseRepository() {

    suspend fun <T> request(apiCall: suspend () -> ApiResponse<T>): Flow<Result<ApiResponse<T>>> = safeApiCall {
        val response = apiCall()
        if (response.isSuccess) {
            response
        } else {
            throw Exception(response.error ?: response.value?.message ?: "Unknown API Error")
        }
    }
}