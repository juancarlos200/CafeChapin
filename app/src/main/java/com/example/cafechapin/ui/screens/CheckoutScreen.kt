package com.example.cafechapin.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cafechapin.R
import com.example.cafechapin.domain.calculateSubtotal
import com.example.cafechapin.domain.calculateTotal
import com.example.cafechapin.model.BillingType
import com.example.cafechapin.model.PaymentMethod
import com.example.cafechapin.ui.StoreUiState
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    storeUiState: StateFlow<StoreUiState>,
    onFullNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNitChange: (String) -> Unit,
    onBusinessNameChange: (String) -> Unit,
    onBillingTypeChange: (BillingType) -> Unit,
    onPaymentMethodChange: (PaymentMethod) -> Unit,
    onConfirmOrder: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val storeState = storeUiState.collectAsStateWithLifecycle()

    val checkout = storeState.value.checkout
    val orderItems = storeState.value.orderItems
    val products = storeState.value.products
    val orderUnits = storeState.value.orderUnits
    val total = calculateTotal(orderItems, products)

    // derivedStateOf evita que el botón dispare recomposición extra cuando
    // el resultado (habilitado/deshabilitado) no cambia, aunque el uiState sí cambie.
    val isConfirmEnabled by remember(storeState) {
        derivedStateOf {
            storeState.value.checkout.isFormValid &&
                    storeState.value.orderUnits > 0
        }
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val nitFocusRequester = remember { FocusRequester() }

    fun clearFocusAndHideKeyboard() {
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                "Total: Q${"%.2f".format(total)}",
                style = MaterialTheme.typography.headlineSmall
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "Resumen del pedido",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text("$orderUnits unidades")

                    orderItems.forEach { item ->
                        val product = products.find { it.id == item.productId }

                        if (product != null) {
                            val subtotal = calculateSubtotal(product, item.quantity)

                            Text(
                                "${product.name} (x${item.quantity}) · Subtotal Q${"%.2f".format(subtotal)}"
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = checkout.fullName,
                onValueChange = onFullNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre completo *") },
                singleLine = true,
                isError = checkout.fullNameTouched && checkout.fullNameError != null,
                supportingText = {
                    if (checkout.fullNameTouched && checkout.fullNameError != null) {
                        Text(checkout.fullNameError.orEmpty())
                    }
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                )
            )

            OutlinedTextField(
                value = checkout.phone,
                onValueChange = onPhoneChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Teléfono / WhatsApp *") },
                singleLine = true,
                isError = checkout.phoneTouched && checkout.phoneError != null,
                supportingText = {
                    if (checkout.phoneTouched && checkout.phoneError != null) {
                        Text(checkout.phoneError.orEmpty())
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = if (checkout.billingType == BillingType.NIT) {
                        ImeAction.Next
                    } else {
                        ImeAction.Done
                    }
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        if (checkout.billingType == BillingType.NIT) {
                            nitFocusRequester.requestFocus()
                        }
                    },
                    onDone = {
                        clearFocusAndHideKeyboard()
                    }
                )
            )

            Text(
                "Facturación *",
                style = MaterialTheme.typography.titleMedium
            )

            Column(Modifier.selectableGroup()) {
                listOf(BillingType.CF, BillingType.NIT).forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .selectable(
                                selected = checkout.billingType == type,
                                role = Role.RadioButton,
                                onClick = {
                                    clearFocusAndHideKeyboard()
                                    onBillingTypeChange(type)
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = checkout.billingType == type,
                            onClick = null
                        )

                        Text(
                            if (type == BillingType.CF) {
                                "Consumidor Final (CF)"
                            } else {
                                "Factura con NIT"
                            }
                        )
                    }
                }
            }

            AnimatedVisibility(visible = checkout.billingType == BillingType.NIT) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "DATOS DE FACTURACIÓN FISCAL",
                        style = MaterialTheme.typography.labelLarge
                    )

                    OutlinedTextField(
                        value = checkout.nit,
                        onValueChange = onNitChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(nitFocusRequester),
                        label = { Text("NIT *") },
                        singleLine = true,
                        isError = checkout.nitTouched && checkout.nitError != null,
                        supportingText = {
                            if (checkout.nitTouched && checkout.nitError != null) {
                                Text(checkout.nitError.orEmpty())
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        )
                    )

                    OutlinedTextField(
                        value = checkout.businessName,
                        onValueChange = onBusinessNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Razón Social / Nombre fiscal *") },
                        singleLine = true,
                        isError = checkout.businessNameTouched && checkout.businessNameError != null,
                        supportingText = {
                            if (checkout.businessNameTouched && checkout.businessNameError != null) {
                                Text(checkout.businessNameError.orEmpty())
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                clearFocusAndHideKeyboard()
                            }
                        )
                    )
                }
            }

            Text(
                "Método de Pago *",
                style = MaterialTheme.typography.titleMedium
            )

            Column(Modifier.selectableGroup()) {
                listOf(
                    PaymentMethod.CASH_ON_DELIVERY,
                    PaymentMethod.BANK_TRANSFER
                ).forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .selectable(
                                selected = checkout.paymentMethod == method,
                                role = Role.RadioButton,
                                onClick = {
                                    clearFocusAndHideKeyboard()
                                    onPaymentMethodChange(method)
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = checkout.paymentMethod == method,
                            onClick = null
                        )

                        Text(
                            if (method == PaymentMethod.CASH_ON_DELIVERY) {
                                "Efectivo contra entrega"
                            } else {
                                "Transferencia bancaria"
                            }
                        )
                    }
                }
            }

            Button(
                onClick = onConfirmOrder,
                enabled = isConfirmEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar pedido (Total Q${"%.2f".format(total)})")
            }

            if (!isConfirmEnabled) {
                Text(
                    "Completa los campos obligatorios para continuar.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
