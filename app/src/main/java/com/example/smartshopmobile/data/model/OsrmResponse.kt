package com.example.smartshopmobile.data.model

import com.google.gson.annotations.SerializedName

data class OsrmResponse(
    @SerializedName("routes") val routes: List<OsrmRoute>
)

data class OsrmRoute(
    @SerializedName("geometry") val geometry: OsrmGeometry,
    @SerializedName("distance") val distance: Double,
    @SerializedName("duration") val duration: Double
)

data class OsrmGeometry(
    @SerializedName("coordinates") val coordinates: List<List<Double>>, // [longitude, latitude]
    @SerializedName("type") val type: String
)
