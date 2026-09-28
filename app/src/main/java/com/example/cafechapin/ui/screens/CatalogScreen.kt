package com.example.cafechapin.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.cafechapin.R
import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.ui.components.ProductImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    products: List<CoffeeProduct>,
    favoriteProductIds: Set<String>,
    query: String,
    orderQuantity: Int,
    gridState: LazyGridState,
    onQueryChange: (String) -> Unit,
    onProductSelected: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenOrder: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val filteredProducts = products.filter { product ->
        product.name.contains(
            query.trim(),
            ignoreCase = true
        )
    }

    val showScrollToTop =
        gridState.firstVisibleItemIndex > 4

    LaunchedEffect(query) {
        gridState.scrollToItem(0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Café Chapín") },
                actions = {
                    Button(
                        onClick = onOpenOrder
                    ) {
                        Text("Pedido · $orderQuantity")
                    }
                }
            )
        },
        floatingActionButton = {
            if (showScrollToTop) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            gridState.animateScrollToItem(0)
                        }
                    }
                ) {
                    Text("↑")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Cafés de Guatemala",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    label = {
                        Text("Buscar productos")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    "${filteredProducts.size} de ${products.size} productos"
                )
            }

            if (filteredProducts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("No encontramos productos.")

                    Button(
                        onClick = {
                            onQueryChange("")
                        }
                    ) {
                        Text("Limpiar búsqueda")
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        top = 4.dp,
                        bottom = 96.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredProducts,
                        key = { product ->
                            product.id
                        }
                    ) { product ->
                        ProductCard(
                            product = product,
                            isFavorite =
                                product.id in favoriteProductIds,
                            onOpen = {
                                onProductSelected(product.id)
                            },
                            onToggleFavorite = {
                                onToggleFavorite(product.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: CoffeeProduct,
    isFavorite: Boolean,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    DisposableEffect(product.id) {
        Log.d(
            "CatalogProbe",
            "ENTER id=${product.id}"
        )

        onDispose {
            Log.d(
                "CatalogProbe",
                "EXIT id=${product.id}"
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            ProductImage(
                imageUrl = product.imageUrl,
                productName = product.name
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onToggleFavorite
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
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

            Text(
                "Q${"%.2f".format(product.price)}"
            )

            Text(
                if (product.stock == 0) {
                    "Agotado"
                } else {
                    "${product.stock} disponibles"
                }
            )

            Button(
                onClick = onOpen,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver detalle")
            }
        }
    }
}