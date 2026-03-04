package com.example.smartshopmobile.data.api

import com.example.smartshopmobile.data.model.AddToCartRequest
import com.example.smartshopmobile.data.model.ApiResponse
import com.example.smartshopmobile.data.model.CartResponse
import com.example.smartshopmobile.data.model.UpdateCartItemRequest
import retrofit2.http.*

interface CartService {
    @POST("api/cart/add")
    suspend fun addToCart(@Body request: AddToCartRequest): ApiResponse<CartResponse>

    @GET("api/cart/my-cart")
    suspend fun getMyCart(): ApiResponse<CartResponse>

    @DELETE("api/cart/items/{cartItemId}")
    suspend fun removeCartItem(@Path("cartItemId") cartItemId: String): ApiResponse<Unit>

    @DELETE("api/cart/clear")
    suspend fun clearCart(): ApiResponse<Unit>

    @PUT("api/cart/items/{cartItemId}")
    suspend fun updateCartItemQuantity(
        @Path("cartItemId") cartItemId: String,
        @Body request: UpdateCartItemRequest
    ): ApiResponse<CartResponse>
}
