package com.example.cafechapin.data.repository

import com.example.cafechapin.model.OrderItem
import kotlinx.coroutines.flow.Flow

interface CurrentOrderRepository {
    fun observeItems(): Flow<List<OrderItem>>
    suspend fun addUnit(productId: String): Result<Unit>
    suspend fun removeUnit(productId: String): Result<Int>
    suspend fun removeLine(productId: String): Result<Int>
    suspend fun clear(): Result<Unit>
}
