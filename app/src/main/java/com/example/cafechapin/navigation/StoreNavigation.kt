package com.example.cafechapin.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.cafechapin.ui.StoreUiState
import com.example.cafechapin.ui.screens.CatalogScreen
import com.example.cafechapin.ui.screens.DetailScreen
import com.example.cafechapin.ui.screens.ProducerProfileScreen

@Composable
fun StoreNavigation(
    uiState: StoreUiState,
    onToggleFavorite: (String) -> Unit
) {
    val backStack = rememberNavBackStack(StoreNavKey.Catalog)

    fun goBack() {
        if (backStack.size > 1) backStack.removeLastOrNull()
    }

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeLastOrNull()
    }

    NavDisplay(
        backStack = backStack,
        onBack = { goBack() },
        entryProvider = entryProvider {
            entry<StoreNavKey.Catalog> {
                CatalogScreen(
                    products = uiState.products,
                    favoriteProductIds = uiState.favoriteProductIds,
                    onProductSelected = { productId ->
                        backStack.add(StoreNavKey.Detail(productId))
                    },
                    onToggleFavorite = onToggleFavorite
                )
            }
            entry<StoreNavKey.Detail> { key ->
                val product = uiState.products.find { it.id == key.productId }
                if (product == null) {
                    Text("Producto no encontrado")
                } else {
                    val producer = uiState.profiles.find { it.id == product.producerId }
                    DetailScreen(
                        product = product,
                        producer = producer,
                        isFavorite = product.id in uiState.favoriteProductIds,
                        onToggleFavorite = { onToggleFavorite(product.id) },
                        onOpenProducer = { profileId ->
                            backStack.add(StoreNavKey.Profile(profileId))
                        },
                        onBack = ::goBack
                    )
                }
            }
            entry<StoreNavKey.Profile> { key ->
                val producer = uiState.profiles.find { it.id == key.profileId }
                if (producer == null) {
                    Text("Perfil no encontrado")
                } else {
                    ProducerProfileScreen(producer = producer, onBack = ::goBack)
                }
            }
        }
    )
}
