package com.example.cafechapin.ui

import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.OrderItem
import com.example.cafechapin.model.OrderReceipt
import com.example.cafechapin.model.ProducerProfile
import com.example.cafechapin.model.ProductSort

data class StoreUiState(
    val catalogState: CatalogUiState = CatalogUiState.Loading,
    val products: List<CoffeeProduct> = emptyList(),
    val profiles: List<ProducerProfile> = emptyList(),
    val favoriteProductIds: Set<String> = emptySet(),
    val query: String = "",
    val sort: ProductSort = ProductSort.CATALOG,
    val orderItems: List<OrderItem> = emptyList(),
    val reservingProductIds: Set<String> = emptySet(),
    val orderMessage: String? = null,
    val checkout: CheckoutUiState = CheckoutUiState(),
    val receipt: OrderReceipt? = null,
    val isDarkTheme: Boolean = false,
    val isConcurrencyTestRunning: Boolean = false,
    val testResult: String? = null
) {
    val orderUnits: Int
        get() = orderItems.sumOf { it.quantity }
}

sealed interface CatalogUiState {

    data object Loading : CatalogUiState

    data object Ready : CatalogUiState

    data class Error(
        val message: String
    ) : CatalogUiState
}
