package com.example.cafechapin.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "order_lines")
data class OrderLineEntity(
    @PrimaryKey
    val productId: String,
    val quantity: Int
)
