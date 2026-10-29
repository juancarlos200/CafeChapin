package com.example.cafechapin.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafechapin.network.StoreApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class OrdersViewModel : ViewModel() {

    private val _ordersState =
        MutableStateFlow<OrdersUiState>(OrdersUiState.Loading)

    val ordersState: StateFlow<OrdersUiState> = _ordersState.asStateFlow()

    private val _detailState =
        MutableStateFlow<OrderDetailUiState>(OrderDetailUiState.Loading)

    val detailState: StateFlow<OrderDetailUiState> = _detailState.asStateFlow()

    fun loadOrders() {
        viewModelScope.launch {
            _ordersState.value = OrdersUiState.Loading
            _ordersState.value = try {
                val orders = StoreApi.orderApi.getOrders()

                if (orders.isEmpty()) {
                    OrdersUiState.Empty
                } else {
                    OrdersUiState.Success(orders)
                }
            } catch (e: IOException) {
                Log.e("OrdersViewModel", "Sin conexión al cargar pedidos", e)
                OrdersUiState.Error("No pudimos cargar tus pedidos. Revisa tu conexión.")
            } catch (e: HttpException) {
                Log.e("OrdersViewModel", "HTTP ${e.code()} al cargar pedidos", e)
                if (e.code() == 404) {
                    OrdersUiState.Empty
                } else {
                    OrdersUiState.Error("El servidor no pudo atender la solicitud.")
                }
            } catch (e: SerializationException) {
                Log.e("OrdersViewModel", "Respuesta inesperada al cargar pedidos", e)
                OrdersUiState.Error("Recibimos una respuesta inesperada.")
            }
        }
    }

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _detailState.value = OrderDetailUiState.Loading
            _detailState.value = try {
                OrderDetailUiState.Success(
                    StoreApi.orderApi.getOrder(orderId)
                )
            } catch (e: IOException) {
                Log.e("OrdersViewModel", "Sin conexión al cargar el pedido", e)
                OrderDetailUiState.Error("No pudimos cargar el pedido. Revisa tu conexión.")
            } catch (e: HttpException) {
                Log.e("OrdersViewModel", "HTTP ${e.code()} al cargar el pedido", e)
                if (e.code() == 404) {
                    OrderDetailUiState.NotFound
                } else {
                    OrderDetailUiState.Error("El servidor no pudo atender la solicitud.")
                }
            } catch (e: SerializationException) {
                Log.e("OrdersViewModel", "Respuesta inesperada al cargar el pedido", e)
                OrderDetailUiState.Error("Recibimos una respuesta inesperada.")
            }
        }
    }
}
