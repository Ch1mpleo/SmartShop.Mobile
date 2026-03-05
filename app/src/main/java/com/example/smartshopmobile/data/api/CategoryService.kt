package com.example.smartshopmobile.data.api

import com.example.smartshopmobile.data.model.ApiResponse
import com.example.smartshopmobile.data.model.CategoryRequest
import com.example.smartshopmobile.data.model.CategoryResponse
import com.example.smartshopmobile.data.model.PaginatedData
import retrofit2.http.*

interface CategoryService {
    @GET("api/categories")
    suspend fun getAllCategories(
        @Query("pageIndex") pageIndex: Int = 0,
        @Query("pageSize") pageSize: Int = 10
    ): ApiResponse<PaginatedData<CategoryResponse>>

    @POST("api/categories")
    suspend fun createCategory(
        @Body request: CategoryRequest
    ): ApiResponse<CategoryResponse>

    @PUT("api/categories/{categoryId}")
    suspend fun updateCategory(
        @Path("categoryId") categoryId: String,
        @Body request: CategoryRequest
    ): ApiResponse<CategoryResponse>

    @DELETE("api/categories/{categoryId}")
    suspend fun deleteCategory(
        @Path("categoryId") categoryId: String
    ): ApiResponse<Boolean>
}