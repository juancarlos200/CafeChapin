package com.example.cafechapin.ui

sealed interface SubmitOrderUiState {

    data object Idle : SubmitOrderUiState

    data object Sending : SubmitOrderUiState

    data object Success : SubmitOrderUiState

    data class Error(
        val message: String
    ) : SubmitOrderUiState
}
