package com.example.cafechapin.ui

import androidx.lifecycle.ViewModel
import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.ProducerProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StoreViewModel : ViewModel() {

    private val products = listOf(
        CoffeeProduct(
            id = "coffee_1",
            name = "Café Antigua",
            description = "Café equilibrado con aroma dulce y notas de chocolate.",
            price = 75.00,
            producerId = "producer_1",
            technicalInfo = "Tueste medio, proceso lavado y cultivo a 1,500 metros."
        ),
        CoffeeProduct(
            id = "coffee_2",
            name = "Café Huehuetenango",
            description = "Café afrutado con acidez brillante y cuerpo suave.",
            price = 85.00,
            producerId = "producer_2",
            technicalInfo = "Tueste medio, proceso natural y cultivo a 1,700 metros."
        ),
        CoffeeProduct(
            id = "coffee_3",
            name = "Café Atitlán",
            description = "Café suave con notas cítricas y florales.",
            price = 80.00,
            producerId = "producer_1",
            technicalInfo = "Tueste claro, proceso lavado y cultivo a 1,600 metros."
        )
    )

    private val profiles = listOf(
        ProducerProfile(
            id = "producer_1",
            name = "Finca Los Volcanes",
            role = "Productor de café",
            location = "Antigua Guatemala",
            description = "Finca familiar dedicada al cultivo responsable de café de especialidad."
        ),
        ProducerProfile(
            id = "producer_2",
            name = "Cooperativa La Sierra",
            role = "Cooperativa productora",
            location = "Huehuetenango",
            description = "Grupo de productores especializados en café cultivado a gran altura."
        )
    )

    private val _uiState = MutableStateFlow(
        StoreUiState(
            products = products,
            profiles = profiles
        )
    )

    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    fun toggleFavorite(productId: String) {
        _uiState.update { currentState ->
            val updatedFavorites =
                if (productId in currentState.favoriteProductIds) {
                    currentState.favoriteProductIds - productId
                } else {
                    currentState.favoriteProductIds + productId
                }

            currentState.copy(
                favoriteProductIds = updatedFavorites
            )
        }
    }
}