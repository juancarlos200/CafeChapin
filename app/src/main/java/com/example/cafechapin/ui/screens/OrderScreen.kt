package com.example.cafechapin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.cafechapin.R
import com.example.cafechapin.domain.calculateSubtotal
import com.example.cafechapin.domain.calculateTotal
import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.OrderItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    orderItems: List<OrderItem>,
    products: List<CoffeeProduct>,
    message: String?,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onRemove: (String) -> Unit,
    onBack: () -> Unit
) {
    val total =
        calculateTotal(orderItems, products)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mi pedido")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector =
                                ImageVector.vectorResource(
                                    R.drawable.ic_arrow_back
                                ),
                            contentDescription =
                                "Regresar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        if (orderItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "No hay productos en tu pedido."
                )

                Text("Total Q0.00")

                Button(
                    onClick = onBack
                ) {
                    Text("Volver al catálogo")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding =
                    androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 48.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                if (message != null) {
                    item {
                        Text(
                            text = message,
                            color =
                                MaterialTheme.colorScheme.error
                        )
                    }
                }

                items(
                    items = orderItems,
                    key = {
                        it.productId
                    }
                ) { item ->

                    val product =
                        products.find {
                            it.id == item.productId
                        }

                    if (product != null) {
                        val subtotal =
                            calculateSubtotal(
                                product,
                                item.quantity
                            )

                        Card(
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier =
                                    Modifier.padding(16.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    product.name,
                                    style =
                                        MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    "Q${"%.2f".format(product.price)} por unidad"
                                )

                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically,
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            onDecrease(
                                                product.id
                                            )
                                        },
                                        modifier =
                                            Modifier.sizeIn(
                                                minWidth = 48.dp,
                                                minHeight = 48.dp
                                            )
                                    ) {
                                        Text("-")
                                    }

                                    Text(
                                        "${item.quantity}"
                                    )

                                    Button(
                                        onClick = {
                                            onIncrease(
                                                product.id
                                            )
                                        },
                                        modifier =
                                            Modifier.sizeIn(
                                                minWidth = 48.dp,
                                                minHeight = 48.dp
                                            )
                                    ) {
                                        Text("+")
                                    }
                                }

                                Text(
                                    "Subtotal Q${"%.2f".format(subtotal)}"
                                )

                                TextButton(
                                    onClick = {
                                        onRemove(
                                            product.id
                                        )
                                    },
                                    modifier =
                                        Modifier.sizeIn(
                                            minHeight = 48.dp
                                        )
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text =
                            "Total Q${"%.2f".format(total)}",
                        style =
                            MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}