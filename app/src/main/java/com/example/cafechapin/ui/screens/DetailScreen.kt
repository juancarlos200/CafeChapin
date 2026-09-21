package com.example.cafechapin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    product: CoffeeProduct,
    producer: ProducerProfile?,
    isFavorite: Boolean,
    orderQuantity: Int,
    orderMessage: String?,
    onToggleFavorite: () -> Unit,
    onOpenProducer: (String) -> Unit,
    onAddToOrder: () -> Unit,
    onOpenOrder: () -> Unit,
    onBack: () -> Unit
) {
    var showTechnicalInfo by remember {
        mutableStateOf(false)
    }

    Scaffold(
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
                        if (product.stock == 0) {
                            "Agotado"
                        } else {
                            "${product.stock} disponibles · $orderQuantity en el pedido"
                        }
                    )
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
                enabled = product.stock > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar al pedido")
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