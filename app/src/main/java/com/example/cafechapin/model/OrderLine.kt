package com.example.cafechapin.model

data class OrderLine(
    val productId: String,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int
)
