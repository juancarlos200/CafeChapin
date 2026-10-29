package com.example.cafechapin.ui

import com.example.cafechapin.model.StoreError

fun Throwable.toUserMessage(): String {
    return when (this) {
        is StoreError.NoConnection -> "Sin conexión. Revisa tu internet e intenta de nuevo."
        is StoreError.Server -> "El servidor no pudo atender la solicitud."
        is StoreError.UnexpectedResponse -> "Recibimos una respuesta inesperada."
        is StoreError.NotFound -> "No encontramos lo que buscabas."
        is StoreError.OutOfStock -> "Este producto está agotado. El pedido no cambió."
        is StoreError.PermissionDenied -> "No tienes permiso para realizar esta acción."
        else -> "Ocurrió un error inesperado. Intenta de nuevo."
    }
}
