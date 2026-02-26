package com.example.smartshopmobile.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

abstract class BaseRepository {

    protected fun <T> safeApiCall(apiCall: suspend () -> T): Flow<Result<T>> = flow {
        try {
            val result = apiCall()
            emit(Result.success(result))
        } catch (e: HttpException) {
            emit(Result.failure(Exception(e.message() ?: "API Error")))
        } catch (e: IOException) {
            emit(Result.failure(Exception("Network Error. Please check your connection.")))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}