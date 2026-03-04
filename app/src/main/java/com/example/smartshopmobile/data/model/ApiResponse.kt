package com.example.smartshopmobile.data.model

data class ApiResponse<T>(
    val isSuccess: Boolean,
    val value: ApiValue<T>?,
    val error: String?
)

data class ApiValue<T>(
    val code: String,
    val message: String,
    val data: T?
)

data class PaginatedData<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val pageSize: Int,
    val totalCount: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean
)
