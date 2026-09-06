package com.example.cafechapin.model

data class CoffeeProduct(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val producerId: String,
    val technicalInfo: String
)