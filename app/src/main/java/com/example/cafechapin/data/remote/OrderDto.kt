package com.example.cafechapin.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: String? = null,
    val createdAt: Long,
    val total: Double,
    val billingType: String,
    val paymentMethod: String,
    val lines: List<OrderLineDto>
)
