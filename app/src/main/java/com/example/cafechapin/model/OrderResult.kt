package com.example.cafechapin.model

sealed class OrderResult {
    data class Success(
        val items: List<OrderItem>
    ) : OrderResult()

    data class Error(
        val message: String
    ) : OrderResult()
}