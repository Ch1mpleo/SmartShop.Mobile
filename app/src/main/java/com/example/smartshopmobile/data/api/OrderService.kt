package com.example.smartshopmobile.data.api

import com.example.smartshopmobile.data.model.ApiResponse
import com.example.smartshopmobile.data.model.CreateOrderRequest
import com.example.smartshopmobile.data.model.OrderResponse
import com.example.smartshopmobile.data.model.PaginatedData
import retrofit2.http.*

interface OrderService {
    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): ApiResponse<OrderResponse>

    @GET("api/orders/my-orders")
    suspend fun getMyOrders(
        @Query("pageIndex") pageIndex: Int = 0,
        @Query("pageSize") pageSize: Int = 10
    ): ApiResponse<PaginatedData<OrderResponse>>

    @POST("api/orders/{orderId}/cancel")
    suspend fun cancelOrder(@Path("orderId") orderId: String): ApiResponse<OrderResponse>
}
