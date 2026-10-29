package com.example.cafechapin.domain

import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.OrderItem

fun validateReservation(product: CoffeeProduct?): String? {
    if (product == null) {
        return "El producto no existe."
    }

    if (product.stock <= 0) {
        return "Este producto está agotado. El pedido no cambió."
    }

    return null
}

fun calculateSubtotal(
    product: CoffeeProduct,
    quantity: Int
): Double {
    return product.price * quantity
}

fun calculateTotal(
    items: List<OrderItem>,
    products: List<CoffeeProduct>
): Double {
    return items.sumOf { item ->
        val product = products.find { it.id == item.productId }

        if (product == null) {
            0.0
        } else {
            calculateSubtotal(product, item.quantity)
        }
    }
}
