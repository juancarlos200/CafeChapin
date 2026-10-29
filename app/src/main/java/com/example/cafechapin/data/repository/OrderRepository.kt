package com.example.cafechapin.data.repository

import com.example.cafechapin.model.BillingType
import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.Order
import com.example.cafechapin.model.OrderItem
import com.example.cafechapin.model.PaymentMethod

interface OrderRepository {
    suspend fun submitOrder(
        items: List<OrderItem>,
        products: List<CoffeeProduct>,
        billingType: BillingType,
        paymentMethod: PaymentMethod
    ): Result<Order>

    suspend fun getOrders(): Result<List<Order>>
    suspend fun getOrder(orderId: String): Result<Order>
}
