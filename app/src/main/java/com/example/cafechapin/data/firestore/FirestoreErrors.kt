package com.example.cafechapin.data.firestore

import com.example.cafechapin.model.StoreError
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException

fun Throwable.toStoreError(): StoreError {
    val causes = generateSequence(this) { it.cause }

    val storeError = causes.filterIsInstance<StoreError>().firstOrNull()

    if (storeError != null) {
        return storeError
    }

    if (causes.any { it is FirebaseNetworkException }) {
        return StoreError.NoConnection(this)
    }

    val firestoreException = causes
        .filterIsInstance<FirebaseFirestoreException>()
        .firstOrNull()
        ?: return StoreError.Unknown(this)

    return when (firestoreException.code) {
        FirebaseFirestoreException.Code.UNAVAILABLE,
        FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> StoreError.NoConnection(this)

        FirebaseFirestoreException.Code.PERMISSION_DENIED,
        FirebaseFirestoreException.Code.UNAUTHENTICATED -> StoreError.PermissionDenied(this)

        FirebaseFirestoreException.Code.NOT_FOUND -> StoreError.NotFound(this)

        else -> StoreError.Server(this)
    }
}
