package com.example.cafechapin.ui

import com.example.cafechapin.model.Order

sealed interface OrdersUiState {

    data object Loading : OrdersUiState

    data object Empty : OrdersUiState

    data class Success(
        val orders: List<Order>
    ) : OrdersUiState

    data class Error(
        val message: String
    ) : OrdersUiState
}
