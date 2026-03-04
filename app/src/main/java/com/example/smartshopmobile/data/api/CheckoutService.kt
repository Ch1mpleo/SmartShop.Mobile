package com.example.smartshopmobile.data.api

import com.example.smartshopmobile.data.model.ApiResponse
import retrofit2.http.POST
import retrofit2.http.Path

interface CheckoutService {
    @POST("api/payment/initiate/{orderId}")
    suspend fun initiatePayment(
        @Path("orderId") orderId: String
    ): ApiResponse<String>
}
