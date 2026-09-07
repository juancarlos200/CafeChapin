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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CafeChapinTheme {
                val storeViewModel: StoreViewModel = viewModel()
                val uiState = storeViewModel.uiState.collectAsStateWithLifecycle().value

                StoreNavigation(
                    uiState = uiState,
                    onToggleFavorite = storeViewModel::toggleFavorite
                )
            }
        }
    }
}
