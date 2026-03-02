package com.example.smartshopmobile.data.api

import com.example.smartshopmobile.data.model.ApiResponse
import com.example.smartshopmobile.data.model.CategoryResponse
import com.example.smartshopmobile.data.model.PaginatedData
import retrofit2.http.GET
import retrofit2.http.Query

interface CategoryService {
    @GET("api/categories")
    suspend fun getAllCategories(
        @Query("pageIndex") pageIndex: Int = 0,
        @Query("pageSize") pageSize: Int = 10
    ): ApiResponse<PaginatedData<CategoryResponse>>
}