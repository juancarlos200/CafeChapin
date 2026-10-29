package com.example.cafechapin.ui

import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.OrderItem
import com.example.cafechapin.model.ProducerProfile

data class StoreUiState(
    val products: List<CoffeeProduct> = emptyList(),
    val profiles: List<ProducerProfile> = emptyList(),
    val favoriteProductIds: Set<String> = emptySet(),
    val query: String = "",
    val orderItems: List<OrderItem> = emptyList(),
    val orderMessage: String? = null
)