package com.example.cafechapin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cafechapin.navigation.StoreNavigation
import com.example.cafechapin.ui.StoreViewModel
import com.example.cafechapin.ui.theme.CafeChapinTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val storeViewModel:
                    StoreViewModel = viewModel()

            val uiState =
                storeViewModel.uiState
                    .collectAsStateWithLifecycle()
                    .value

            CafeChapinTheme(
                darkTheme = uiState.isDarkTheme
            ) {
                StoreNavigation(
                    uiState = uiState,
                    storeUiState = storeViewModel.uiState,
                    onToggleFavorite =
                        storeViewModel::toggleFavorite,
                    onToggleDarkTheme =
                        storeViewModel::toggleDarkTheme,
                    onQueryChange =
                        storeViewModel::updateQuery,
                    onAddToOrder =
                        storeViewModel::addProductToOrder,
                    onIncreaseOrderItem =
                        storeViewModel::increaseOrderItem,
                    onDecreaseOrderItem =
                        storeViewModel::decreaseOrderItem,
                    onRemoveOrderItem =
                        storeViewModel::removeOrderItem,
                    onFullNameChange =
                        storeViewModel::onFullNameChange,
                    onPhoneChange =
                        storeViewModel::onPhoneChange,
                    onNitChange =
                        storeViewModel::onNitChange,
                    onBusinessNameChange =
                        storeViewModel::onBusinessNameChange,
                    onBillingTypeChange =
                        storeViewModel::onBillingTypeChange,
                    onPaymentMethodChange =
                        storeViewModel::onPaymentMethodChange,
                    onConfirmOrder =
                        storeViewModel::confirmOrder
                )
            }
        }
    }
}