package com.example.cafechapin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cafechapin.model.BillingType
import com.example.cafechapin.model.OrderReceipt
import com.example.cafechapin.model.PaymentMethod

@Composable
fun ConfirmationScreen(
    receipt: OrderReceipt?,
    onBackToCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            "✓",
            style = MaterialTheme.typography.displayLarge
        )

        Text(
            "¡Pedido confirmado!",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("Orden registrada exitosamente en su tienda.")

        if (receipt == null) {
            Text("No hay un pedido confirmado.")
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Folio: ${receipt.folio}")

                    Text("Cliente: ${receipt.customerName}")

                    if (receipt.billingType == BillingType.CF) {
                        Text("Facturación: CF (Consumidor Final)")
                    } else {
                        Text("Facturación: Factura con NIT")
                        Text("NIT: ${receipt.nit.orEmpty()}")
                        Text("Razón Social: ${receipt.businessName.orEmpty()}")
                    }

                    Text(
                        if (receipt.paymentMethod == PaymentMethod.CASH_ON_DELIVERY) {
                            "Método de pago: Efectivo contra entrega"
                        } else {
                            "Método de pago: Transferencia bancaria"
                        }
                    )

                    Text(
                        "Total del pedido: Q${"%.2f".format(receipt.total)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        Button(
            onClick = onBackToCatalog,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al catálogo")
        }
    }
}
