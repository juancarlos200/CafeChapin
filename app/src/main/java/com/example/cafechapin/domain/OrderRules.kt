package com.example.cafechapin.domain

import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.OrderItem
import com.example.cafechapin.model.OrderResult

fun addToOrder(
    items: List<OrderItem>,
    products: List<CoffeeProduct>,
    productId: String,
    amount: Int = 1
): OrderResult {
    if (amount <= 0) {
        return OrderResult.Error("La cantidad debe ser mayor que cero.")
    }

    val product = products.find { it.id == productId }
        ?: return OrderResult.Error("El producto no existe.")

    if (product.stock == 0) {
        return OrderResult.Error("Este producto está agotado.")
    }

    val currentItem = items.find { it.productId == productId }
    val currentQuantity = currentItem?.quantity ?: 0
    val newQuantity = currentQuantity + amount

    if (newQuantity > product.stock) {
        return OrderResult.Error(
            "Solo hay ${product.stock} unidades disponibles. El pedido no cambió."
        )
    }

    val updatedItems =
        if (currentItem == null) {
            items + OrderItem(
                productId = productId,
                quantity = newQuantity
            )
        } else {
            items.map { item ->
                if (item.productId == productId) {
                    item.copy(quantity = newQuantity)
                } else {
                    item
                }
            }
        }

    return OrderResult.Success(updatedItems)
}

fun decreaseItem(
    items: List<OrderItem>,
    productId: String
): List<OrderItem> {
    val item = items.find { it.productId == productId }
        ?: return items

    if (item.quantity <= 1) {
        return items.filterNot { it.productId == productId }
    }

    return items.map { current ->
        if (current.productId == productId) {
            current.copy(quantity = current.quantity - 1)
        } else {
            current
        }
    }
}

fun removeItem(
    items: List<OrderItem>,
    productId: String
): List<OrderItem> {
    return items.filterNot { it.productId == productId }
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