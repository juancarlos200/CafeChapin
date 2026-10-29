package com.example.cafechapin.model

data class Order(
    val id: String,
    val createdAt: Long,
    val total: Double,
    val billingType: BillingType,
    val paymentMethod: PaymentMethod,
    val lines: List<OrderLine>
)
