package com.example.cafechapin.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class OrderLineDto(
    val productId: String,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int
)
