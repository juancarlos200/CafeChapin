package com.example.cafechapin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cafechapin.CafeChapinApplication
import com.example.cafechapin.data.repository.OrderRepository
import com.example.cafechapin.model.StoreError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _ordersState =
        MutableStateFlow<OrdersUiState>(OrdersUiState.Loading)

    val ordersState: StateFlow<OrdersUiState> = _ordersState.asStateFlow()

    private val _detailState =
        MutableStateFlow<OrderDetailUiState>(OrderDetailUiState.Loading)

    val detailState: StateFlow<OrderDetailUiState> = _detailState.asStateFlow()

    fun loadOrders() {
        viewModelScope.launch {
            _ordersState.value = OrdersUiState.Loading
            _ordersState.value = orderRepository.getOrders().fold(
                onSuccess = { orders ->
                    if (orders.isEmpty()) {
                        OrdersUiState.Empty
                    } else {
                        OrdersUiState.Success(orders)
                    }
                },
                onFailure = { error ->
                    OrdersUiState.Error(error.toUserMessage())
                }
            )
        }
    }

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _detailState.value = OrderDetailUiState.Loading
            _detailState.value = orderRepository.getOrder(orderId).fold(
                onSuccess = { order ->
                    OrderDetailUiState.Success(order)
                },
                onFailure = { error ->
                    if (error is StoreError.NotFound) {
                        OrderDetailUiState.NotFound
                    } else {
                        OrderDetailUiState.Error(error.toUserMessage())
                    }
                }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CafeChapinApplication
                OrdersViewModel(application.container.orderRepository)
            }
        }
    }
}
