package com.example.smartshopmobile.data.api

import com.example.smartshopmobile.data.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CheckoutService {
    @POST("api/payment/initiate/{orderId}")
    suspend fun initiatePayment(
        @Path("orderId") orderId: String
    ): ApiResponse<String>

    @POST("api/payment/checkout/{orderId}")
    suspend fun createCheckoutSession(
        @Path("orderId") orderId: String
    ): ApiResponse<String>

    @GET("api/payment/verify")
    suspend fun verifyPayment(
        @Query("sessionId") sessionId: String
    ): ApiResponse<Boolean>
}
