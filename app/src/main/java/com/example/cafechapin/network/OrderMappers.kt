package com.example.cafechapin.network

import com.example.cafechapin.domain.calculateTotal
import com.example.cafechapin.model.BillingType
import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.OrderItem
import com.example.cafechapin.model.PaymentMethod

fun createOrderDto(
    items: List<OrderItem>,
    products: List<CoffeeProduct>,
    billingType: BillingType,
    paymentMethod: PaymentMethod,
    createdAt: Long
): OrderDto {
    val lines = items.mapNotNull { item ->
        val product = products.find { it.id == item.productId }

        if (product == null) {
            null
        } else {
            OrderLineDto(
                productId = product.id,
                productName = product.name,
                unitPrice = product.price,
                quantity = item.quantity
            )
        }
    }

    return OrderDto(
        createdAt = createdAt,
        total = calculateTotal(items, products),
        billingType = billingType.name,
        paymentMethod = paymentMethod.name,
        lines = lines
    )
}
