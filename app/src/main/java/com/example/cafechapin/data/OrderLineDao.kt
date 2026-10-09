package com.example.cafechapin.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderLineDao {

    @Query("SELECT * FROM order_lines")
    fun getOrderLines(): Flow<List<OrderLineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orderLine: OrderLineEntity)

    @Query("DELETE FROM order_lines WHERE productId = :productId")
    suspend fun deleteByProductId(productId: String)

    @Query("DELETE FROM order_lines")
    suspend fun clearOrder()
}
