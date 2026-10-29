package com.example.cafechapin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.cafechapin.R
import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.ProducerProfile
import com.example.cafechapin.ui.components.ProductImage

const val CONCURRENCY_TEST_ATTEMPTS = 20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    product: CoffeeProduct,
    producer: ProducerProfile?,
    isFavorite: Boolean,
    orderQuantity: Int,
    orderMessage: String?,
    isReserving: Boolean,
    isConcurrencyTestRunning: Boolean,
    testResult: String?,
    onToggleFavorite: () -> Unit,
    onOpenProducer: (String) -> Unit,
    onAddToOrder: () -> Unit,
    onOpenOrder: () -> Unit,
    onConcurrencyTest: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTechnicalInfo by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector =
                                ImageVector.vectorResource(
                                    R.drawable.ic_arrow_back
                                ),
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onOpenOrder
                    ) {
                        Text("Pedido · $orderQuantity")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(
                    rememberScrollState()
                ),
            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            ProductImage(
                imageUrl = product.imageUrl,
                productName = product.name,
                imageHeight = 220.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        product.name,
                        style =
                            MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        "Q${"%.2f".format(product.price)}",
                        style =
                            MaterialTheme.typography.titleLarge
                    )

                    Text(
                        if (product.stock <= 0) {
                            "Agotado"
                        } else {
                            "${product.stock} disponibles"
                        },
                        color = if (product.stock <= 0) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    Text("$orderQuantity en tu pedido")
                }

                IconButton(
                    onClick = onToggleFavorite
                ) {
                    Icon(
                        imageVector =
                            ImageVector.vectorResource(
                                if (isFavorite) {
                                    R.drawable.ic_favorite
                                } else {
                                    R.drawable.ic_favorite_border
                                }
                            ),
                        contentDescription =
                            if (isFavorite) {
                                "Quitar de favoritos"
                            } else {
                                "Agregar a favoritos"
                            }
                    )
                }
            }

            Text(product.description)

            Button(
                onClick = onAddToOrder,
                enabled = !isReserving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isReserving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Text(
                        "Reservando unidad…",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                } else {
                    Text("Agregar al pedido")
                }
            }

            if (orderMessage != null) {
                Text(orderMessage)
            }

            OutlinedButton(
                onClick = onOpenOrder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver pedido")
            }

            OutlinedButton(
                onClick = {
                    showTechnicalInfo =
                        !showTechnicalInfo
                }
            ) {
                Text(
                    if (showTechnicalInfo) {
                        "Ocultar ficha técnica"
                    } else {
                        "Ver ficha técnica"
                    }
                )
            }

            if (showTechnicalInfo) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Ficha técnica",
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Text(product.technicalInfo)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Herramienta de prueba",
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    OutlinedButton(
                        onClick = onConcurrencyTest,
                        enabled = !isConcurrencyTestRunning,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isConcurrencyTestRunning) {
                                "Ejecutando prueba…"
                            } else {
                                "Prueba de concurrencia · $CONCURRENCY_TEST_ATTEMPTS intentos"
                            }
                        )
                    }

                    if (testResult != null) {
                        Text(testResult)
                    }
                }
            }

            producer?.let {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Productor asociado",
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Text(it.name)
                        Text(it.location)

                        Button(
                            onClick = {
                                onOpenProducer(it.id)
                            },
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Ver perfil del productor"
                            )
                        }
                    }
                }
            }
        }
    }
}