package com.example.cafechapin.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.cafechapin.model.BillingType
import com.example.cafechapin.model.PaymentMethod
import com.example.cafechapin.ui.StoreUiState
import com.example.cafechapin.ui.screens.CatalogScreen
import com.example.cafechapin.ui.screens.CheckoutScreen
import com.example.cafechapin.ui.screens.ConfirmationScreen
import com.example.cafechapin.ui.screens.DetailScreen
import com.example.cafechapin.ui.screens.OrderScreen
import com.example.cafechapin.ui.screens.ProducerProfileScreen
import kotlinx.coroutines.flow.StateFlow

@Composable
fun StoreNavigation(
    uiState: StoreUiState,
    storeUiState: StateFlow<StoreUiState>,
    onToggleFavorite: (String) -> Unit,
    onToggleDarkTheme: () -> Unit,
    onQueryChange: (String) -> Unit,
    onAddToOrder: (String) -> Unit,
    onIncreaseOrderItem: (String) -> Unit,
    onDecreaseOrderItem: (String) -> Unit,
    onRemoveOrderItem: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNitChange: (String) -> Unit,
    onBusinessNameChange: (String) -> Unit,
    onBillingTypeChange: (BillingType) -> Unit,
    onPaymentMethodChange: (PaymentMethod) -> Unit,
    onConfirmOrder: () -> Boolean
) {
    val backStack =
        rememberNavBackStack(
            StoreNavKey.Catalog
        )

    val catalogGridState = rememberLazyGridState()

    val totalOrderQuantity =
        uiState.orderItems.sumOf {
            it.quantity
        }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    BackHandler(
        enabled = backStack.size > 1
    ) {
        goBack()
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            goBack()
        },
        entryProvider = entryProvider {

            entry<StoreNavKey.Catalog> {
                CatalogScreen(
                    products =
                        uiState.products,
                    favoriteProductIds =
                        uiState.favoriteProductIds,
                    query =
                        uiState.query,
                    isDarkTheme =
                        uiState.isDarkTheme,
                    orderQuantity =
                        totalOrderQuantity,
                    gridState =
                        catalogGridState,
                    onQueryChange =
                        onQueryChange,
                    onProductSelected = {
                            productId ->
                        backStack.add(
                            StoreNavKey.Detail(
                                productId
                            )
                        )
                    },
                    onToggleFavorite =
                        onToggleFavorite,
                    onToggleDarkTheme =
                        onToggleDarkTheme,
                    onOpenOrder = {
                        backStack.add(
                            StoreNavKey.Order
                        )
                    }
                )
            }

            entry<StoreNavKey.Detail> { key ->
                val product =
                    uiState.products.find {
                        it.id == key.productId
                    }

                if (product == null) {
                    Text(
                        "Producto no encontrado"
                    )
                } else {
                    val producer =
                        uiState.profiles.find {
                            it.id ==
                                    product.producerId
                        }

                    val quantityInOrder =
                        uiState.orderItems
                            .find {
                                it.productId ==
                                        product.id
                            }
                            ?.quantity ?: 0

                    DetailScreen(
                        product = product,
                        producer = producer,
                        isFavorite =
                            product.id in
                                    uiState.favoriteProductIds,
                        orderQuantity =
                            quantityInOrder,
                        orderMessage =
                            uiState.orderMessage,
                        onToggleFavorite = {
                            onToggleFavorite(
                                product.id
                            )
                        },
                        onOpenProducer = {
                                profileId ->
                            backStack.add(
                                StoreNavKey.Profile(
                                    profileId
                                )
                            )
                        },
                        onAddToOrder = {
                            onAddToOrder(
                                product.id
                            )
                        },
                        onOpenOrder = {
                            backStack.add(
                                StoreNavKey.Order
                            )
                        },
                        onBack = ::goBack
                    )
                }
            }

            entry<StoreNavKey.Profile> { key ->
                val producer =
                    uiState.profiles.find {
                        it.id ==
                                key.profileId
                    }

                if (producer == null) {
                    Text(
                        "Perfil no encontrado"
                    )
                } else {
                    ProducerProfileScreen(
                        producer = producer,
                        onBack = ::goBack
                    )
                }
            }

            entry<StoreNavKey.Order> {
                OrderScreen(
                    orderItems =
                        uiState.orderItems,
                    products =
                        uiState.products,
                    message =
                        uiState.orderMessage,
                    onIncrease =
                        onIncreaseOrderItem,
                    onDecrease =
                        onDecreaseOrderItem,
                    onRemove =
                        onRemoveOrderItem,
                    onContinueToCheckout = {
                        backStack.add(
                            StoreNavKey.Checkout
                        )
                    },
                    onBack = ::goBack
                )
            }

            entry<StoreNavKey.Checkout> {
                CheckoutScreen(
                    storeUiState = storeUiState,
                    onFullNameChange = onFullNameChange,
                    onPhoneChange = onPhoneChange,
                    onNitChange = onNitChange,
                    onBusinessNameChange = onBusinessNameChange,
                    onBillingTypeChange = onBillingTypeChange,
                    onPaymentMethodChange = onPaymentMethodChange,
                    onConfirmOrder = {
                        val success = onConfirmOrder()

                        if (success) {
                            backStack.add(
                                StoreNavKey.Confirmation
                            )
                        }
                    },
                    onBack = ::goBack
                )
            }

            entry<StoreNavKey.Confirmation> {
                ConfirmationScreen(
                    receipt = uiState.receipt,
                    onBackToCatalog = {
                        backStack.clear()
                        backStack.add(
                            StoreNavKey.Catalog
                        )
                    }
                )
            }
        }
    )
}