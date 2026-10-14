package com.example.cafechapin.ui

import com.example.cafechapin.network.OrderDto

sealed interface OrdersUiState {

    data object Loading : OrdersUiState

    data object Empty : OrdersUiState

    data class Success(
        val orders: List<OrderDto>
    ) : OrdersUiState

    data class Error(
        val message: String
    ) : OrdersUiState
}
