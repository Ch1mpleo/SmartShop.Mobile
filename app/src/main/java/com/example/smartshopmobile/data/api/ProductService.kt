package com.example.smartshopmobile.data.api

import com.example.smartshopmobile.data.model.ApiResponse
import com.example.smartshopmobile.data.model.PaginatedData
import com.example.smartshopmobile.data.model.ProductRequest
import com.example.smartshopmobile.data.model.ProductResponse
import retrofit2.http.*

interface ProductService {
    @GET("api/products")
    suspend fun getProducts(
        @Query("search") search: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("isDescending") isDescending: Boolean = true,
        @Query("pageIndex") pageIndex: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("categoryId") categoryId: String? = null,
        @Query("productId") productId: String? = null,
        @Query("brand") brand: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("minRating") minRating: Double? = null
    ): ApiResponse<PaginatedData<ProductResponse>>

    @GET("api/products/{productId}")
    suspend fun getProductById(
        @Path("productId") productId: String
    ): ApiResponse<ProductResponse>

    @POST("api/products")
    suspend fun createProduct(
        @Body request: ProductRequest
    ): ApiResponse<ProductResponse>

    @PUT("api/products/{productId}")
    suspend fun updateProduct(
        @Path("productId") productId: String,
        @Body request: ProductRequest
    ): ApiResponse<ProductResponse>

    @DELETE("api/products/{productId}")
    suspend fun deleteProduct(
        @Path("productId") productId: String
    ): ApiResponse<Boolean>
}
