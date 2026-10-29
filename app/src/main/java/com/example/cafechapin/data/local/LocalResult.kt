package com.example.cafechapin.data.local

import com.example.cafechapin.model.StoreError
import kotlinx.coroutines.CancellationException

suspend fun <T> runLocal(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(StoreError.Unknown(e))
    }
}
