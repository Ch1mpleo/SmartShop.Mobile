package com.example.smartshopmobile.data.api

import com.example.smartshopmobile.data.model.ApiResponse
import com.example.smartshopmobile.data.model.PaginatedData
import com.example.smartshopmobile.data.model.StoreLocationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StoreLocationService {
    @GET("api/store-locations")
    suspend fun getAllStoreLocations(
        @Query("pageIndex") pageIndex: Int = 0,
        @Query("pageSize") pageSize: Int = 10
    ): ApiResponse<PaginatedData<StoreLocationResponse>>

    @GET("api/store-locations/nearest")
    suspend fun getNearestStoreLocations(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("count") count: Int = 5
    ): ApiResponse<List<StoreLocationResponse>>
}