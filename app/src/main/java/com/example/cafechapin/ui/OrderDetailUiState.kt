package com.example.cafechapin.ui

import com.example.cafechapin.model.Order

sealed interface OrderDetailUiState {

    data object Loading : OrderDetailUiState

    data object NotFound : OrderDetailUiState

    data class Success(
        val order: Order
    ) : OrderDetailUiState

    data class Error(
        val message: String
    ) : OrderDetailUiState
}
