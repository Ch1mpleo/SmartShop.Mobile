package com.example.smartshopmobile.data.api

import com.example.smartshopmobile.data.model.ApiResponse
import com.example.smartshopmobile.data.model.PaginatedData
import com.example.smartshopmobile.data.model.ProductResponse
import com.example.smartshopmobile.data.model.ProductStorageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductService {
    @GET("api/products")
    suspend fun getProducts(
        @Query("search") search: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("isDescending") isDescending: Boolean = true,
        @Query("pageIndex") pageIndex: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("categoryId") categoryId: String? = null,
        @Query("productId") productId: String? = null
    ): ApiResponse<PaginatedData<ProductResponse>>

    @GET("api/product-storages")
    suspend fun getProductStorages(
        @Query("search") search: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("isDescending") isDescending: Boolean = true,
        @Query("pageIndex") pageIndex: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): ApiResponse<PaginatedData<ProductStorageResponse>>
}