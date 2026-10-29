package com.example.cafechapin.ui.screens

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.cafechapin.R
import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.ProductSort
import com.example.cafechapin.ui.CatalogUiState
import com.example.cafechapin.ui.components.ProductImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    catalogState: CatalogUiState,
    products: List<CoffeeProduct>,
    favoriteProductIds: Set<String>,
    query: String,
    sort: ProductSort,
    isDarkTheme: Boolean,
    orderQuantity: Int,
    gridState: LazyGridState,
    onQueryChange: (String) -> Unit,
    onSortChange: (ProductSort) -> Unit,
    onRetry: () -> Unit,
    onProductSelected: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleDarkTheme: () -> Unit,
    onOpenOrder: () -> Unit,
    onOpenOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val visibleProducts = products
        .filter { product ->
            product.name.contains(
                query.trim(),
                ignoreCase = true
            )
        }
        .sortedWith(sort.comparator())

    val showScrollToTop =
        gridState.firstVisibleItemIndex > 4

    LaunchedEffect(query, sort) {
        gridState.scrollToItem(0)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Café Chapín") },
                actions = {
                    TextButton(
                        onClick = onOpenOrders
                    ) {
                        Text("Mis pedidos")
                    }

                    IconButton(
                        onClick = onToggleDarkTheme
                    ) {
                        Text(
                            if (isDarkTheme) {
                                "☀"
                            } else {
                                "☾"
                            }
                        )
                    }

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
            when (catalogState) {
                CatalogUiState.Loading -> {
                    CatalogMessage(
                        message = "Cargando catálogo…",
                        showProgress = true
                    )
                }

                is CatalogUiState.Error -> {
                    CatalogMessage(
                        message = catalogState.message,
                        actionLabel = "Reintentar",
                        onAction = onRetry
                    )
                }

                CatalogUiState.Ready -> {
                    CatalogHeader(
                        query = query,
                        sort = sort,
                        visibleCount = visibleProducts.size,
                        totalCount = products.size,
                        onQueryChange = onQueryChange,
                        onSortChange = onSortChange
                    )

                    if (visibleProducts.isEmpty()) {
                        CatalogMessage(
                            message = "No encontramos productos.",
                            actionLabel = "Limpiar búsqueda",
                            onAction = {
                                onQueryChange("")
                            }
                        )
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
                                items = visibleProducts,
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
    }
}

private fun ProductSort.comparator(): Comparator<CoffeeProduct> {
    return when (this) {
        ProductSort.CATALOG -> compareBy { 0 }
        ProductSort.NAME -> compareBy { it.name.lowercase() }
        ProductSort.PRICE_ASCENDING -> compareBy { it.price }
        ProductSort.PRICE_DESCENDING -> compareByDescending { it.price }
    }
}

private fun ProductSort.label(): String {
    return when (this) {
        ProductSort.CATALOG -> "Catálogo"
        ProductSort.NAME -> "Nombre"
        ProductSort.PRICE_ASCENDING -> "Precio ↑"
        ProductSort.PRICE_DESCENDING -> "Precio ↓"
    }
}

@Composable
private fun CatalogHeader(
    query: String,
    sort: ProductSort,
    visibleCount: Int,
    totalCount: Int,
    onQueryChange: (String) -> Unit,
    onSortChange: (ProductSort) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
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

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProductSort.entries.forEach { option ->
                FilterChip(
                    selected = option == sort,
                    onClick = {
                        onSortChange(option)
                    },
                    label = {
                        Text(option.label())
                    }
                )
            }
        }

        Text(
            "$visibleCount de $totalCount productos"
        )
    }
}

@Composable
private fun CatalogMessage(
    message: String,
    modifier: Modifier = Modifier,
    showProgress: Boolean = false,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            12.dp,
            Alignment.CenterVertically
        )
    ) {
        if (showProgress) {
            CircularProgressIndicator()
        }

        Text(message)

        if (actionLabel != null) {
            Button(
                onClick = onAction
            ) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: CoffeeProduct,
    isFavorite: Boolean,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
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
                if (product.stock <= 0) {
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
