package com.example.cafechapin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.cafechapin.R
import com.example.cafechapin.ui.OrderDetailUiState
import com.example.cafechapin.ui.components.formatOrderDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    state: OrderDetailUiState,
    onRetry: () -> Unit,
    onBackToOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detalle del pedido") },
                navigationIcon = {
                    IconButton(onClick = onBackToOrders) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                R.drawable.ic_arrow_back
                            ),
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (state) {
            OrderDetailUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            OrderDetailUiState.NotFound -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        12.dp,
                        Alignment.CenterVertically
                    )
                ) {
                    Text(
                        "Pedido no encontrado",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text("Este pedido ya no existe en el servidor.")

                    Button(onClick = onBackToOrders) {
                        Text("Volver a Mis pedidos")
                    }
                }
            }

            is OrderDetailUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        12.dp,
                        Alignment.CenterVertically
                    )
                ) {
                    Text(
                        state.message,
                        color = MaterialTheme.colorScheme.error
                    )

                    Button(onClick = onRetry) {
                        Text("Reintentar")
                    }
                }
            }

            is OrderDetailUiState.Success -> {
                val order = state.order

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Pedido #${order.id}",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text("Fecha: ${formatOrderDate(order.createdAt)}")

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            order.lines.forEach { line ->
                                Text(
                                    "${line.productName} (x${line.quantity}) · " +
                                            "Q${"%.2f".format(line.unitPrice * line.quantity)}"
                                )
                            }
                        }
                    }

                    Text(
                        "Total: Q${"%.2f".format(order.total)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
