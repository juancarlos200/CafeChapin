package com.example.cafechapin.ui

import com.example.cafechapin.network.OrderDto

sealed interface OrderDetailUiState {

    data object Loading : OrderDetailUiState

    data object NotFound : OrderDetailUiState

    data class Success(
        val order: OrderDto
    ) : OrderDetailUiState

    data class Error(
        val message: String
    ) : OrderDetailUiState
}
