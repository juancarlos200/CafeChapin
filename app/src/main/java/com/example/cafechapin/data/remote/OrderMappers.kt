package com.example.cafechapin.data.remote

import com.example.cafechapin.domain.calculateTotal
import com.example.cafechapin.model.BillingType
import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.Order
import com.example.cafechapin.model.OrderItem
import com.example.cafechapin.model.OrderLine
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

fun OrderDto.toOrder(): Order {
    return Order(
        id = id.orEmpty(),
        createdAt = createdAt,
        total = total,
        billingType = BillingType.entries.find { it.name == billingType } ?: BillingType.CF,
        paymentMethod = PaymentMethod.entries.find { it.name == paymentMethod }
            ?: PaymentMethod.CASH_ON_DELIVERY,
        lines = lines.map { line ->
            OrderLine(
                productId = line.productId,
                productName = line.productName,
                unitPrice = line.unitPrice,
                quantity = line.quantity
            )
        }
    )
}
