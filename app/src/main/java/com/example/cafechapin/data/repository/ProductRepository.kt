package com.example.cafechapin.data.repository

import com.example.cafechapin.model.CoffeeProduct
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProducts(): Flow<List<CoffeeProduct>>
    suspend fun reserveUnit(productId: String): Result<Unit>
    suspend fun releaseUnits(productId: String, quantity: Int): Result<Unit>
}
