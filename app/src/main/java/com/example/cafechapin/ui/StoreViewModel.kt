package com.example.cafechapin.ui

import androidx.lifecycle.ViewModel
import com.example.cafechapin.domain.addToOrder
import com.example.cafechapin.domain.decreaseItem
import com.example.cafechapin.domain.removeItem
import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.OrderResult
import com.example.cafechapin.model.ProducerProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class StoreViewModel : ViewModel() {

    private val products = createProducts()

    private fun createProducts(): List<CoffeeProduct> {
        val originalProducts = listOf(
            CoffeeProduct(
                id = "coffee_1",
                name = "Café Antigua",
                description = "Café equilibrado con aroma dulce y notas de chocolate.",
                price = 75.00,
                producerId = "producer_1",
                technicalInfo = "Tueste medio, proceso lavado y cultivo a 1,500 metros.",
                stock = 3,
                imageUrl = "https://picsum.photos/seed/coffee-1/400/400"
            ),
            CoffeeProduct(
                id = "coffee_2",
                name = "Café Huehuetenango",
                description = "Café afrutado con acidez brillante y cuerpo suave.",
                price = 85.00,
                producerId = "producer_2",
                technicalInfo = "Tueste medio, proceso natural y cultivo a 1,700 metros.",
                stock = 8,
                imageUrl = "https://picsum.photos/seed/coffee-2/400/400"
            ),
            CoffeeProduct(
                id = "coffee_3",
                name = "Café Atitlán",
                description = "Café suave con notas cítricas y florales.",
                price = 80.00,
                producerId = "producer_1",
                technicalInfo = "Tueste claro, proceso lavado y cultivo a 1,600 metros.",
                stock = 0,
                imageUrl = "https://picsum.photos/seed/coffee-3/400/400"
            )
        )

        val random = Random(1234)

        val regions = listOf(
            "Antigua",
            "Huehuetenango",
            "Atitlán",
            "Cobán",
            "Fraijanes"
        )

        val roasts = listOf(
            "claro",
            "medio",
            "oscuro"
        )

        val generatedProducts = (4..500).map { number ->
            val region = regions.random(random)
            val roast = roasts.random(random)

            CoffeeProduct(
                id = "generated_$number",
                name = "Café $region $number",
                description = "Café de $region con tueste $roast.",
                price = random.nextInt(50, 121).toDouble(),
                producerId = if (number % 2 == 0) {
                    "producer_1"
                } else {
                    "producer_2"
                },
                technicalInfo = "Tueste $roast, café cultivado en $region.",
                stock = random.nextInt(0, 11),
                imageUrl = "https://picsum.photos/seed/generated-$number/400/400"
            )
        }

        return originalProducts + generatedProducts
    }

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

    fun updateQuery(query: String) {
        _uiState.update { currentState ->
            currentState.copy(query = query)
        }
    }

    fun addProductToOrder(productId: String) {
        val currentState = _uiState.value

        when (
            val result = addToOrder(
                items = currentState.orderItems,
                products = currentState.products,
                productId = productId
            )
        ) {
            is OrderResult.Success -> {
                _uiState.update {
                    it.copy(
                        orderItems = result.items,
                        orderMessage = "Se agregó 1 unidad al pedido."
                    )
                }
            }

            is OrderResult.Error -> {
                _uiState.update {
                    it.copy(orderMessage = result.message)
                }
            }
        }
    }

    fun increaseOrderItem(productId: String) {
        val currentState = _uiState.value

        when (
            val result = addToOrder(
                items = currentState.orderItems,
                products = currentState.products,
                productId = productId
            )
        ) {
            is OrderResult.Success -> {
                _uiState.update {
                    it.copy(
                        orderItems = result.items,
                        orderMessage = null
                    )
                }
            }

            is OrderResult.Error -> {
                _uiState.update {
                    it.copy(orderMessage = result.message)
                }
            }
        }
    }

    fun decreaseOrderItem(productId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                orderItems = decreaseItem(
                    items = currentState.orderItems,
                    productId = productId
                ),
                orderMessage = null
            )
        }
    }

    fun removeOrderItem(productId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                orderItems = removeItem(
                    items = currentState.orderItems,
                    productId = productId
                ),
                orderMessage = null
            )
        }
    }
}