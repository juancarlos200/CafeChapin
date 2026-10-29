package com.example.cafechapin.data.firestore

import com.example.cafechapin.data.repository.ProductRepository
import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.StoreError
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.TransactionOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val MAX_TRANSACTION_ATTEMPTS = 30

class FirestoreProductRepository(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    private val products = firestore.collection(PRODUCTS_COLLECTION)

    private val transactionOptions = TransactionOptions.Builder()
        .setMaxAttempts(MAX_TRANSACTION_ATTEMPTS)
        .build()

    override fun observeProducts(): Flow<List<CoffeeProduct>> = callbackFlow {
        val registration = products.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error.toStoreError())
                return@addSnapshotListener
            }

            if (snapshot == null) {
                return@addSnapshotListener
            }

            if (snapshot.isEmpty && snapshot.metadata.isFromCache) {
                close(StoreError.NoConnection())
                return@addSnapshotListener
            }

            trySend(
                snapshot.documents
                    .mapNotNull { it.toCoffeeProduct() }
                    .sortedBy { it.catalogPosition() }
            )
        }

        awaitClose { registration.remove() }
    }

    override suspend fun reserveUnit(productId: String): Result<Unit> {
        val document = products.document(productId)

        return try {
            firestore.runTransaction(transactionOptions) { transaction ->
                val stock = transaction.get(document).getLong(STOCK_FIELD)
                    ?: throw StoreError.NotFound()

                if (stock <= 0) {
                    throw StoreError.OutOfStock()
                }

                transaction.update(document, STOCK_FIELD, stock - 1)
            }.await()

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toStoreError())
        }
    }

    override suspend fun releaseUnits(productId: String, quantity: Int): Result<Unit> {
        if (quantity <= 0) {
            return Result.success(Unit)
        }

        return try {
            products.document(productId)
                .update(STOCK_FIELD, FieldValue.increment(quantity.toLong()))
                .await()

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toStoreError())
        }
    }

    private fun CoffeeProduct.catalogPosition(): Int {
        return id.substringAfterLast("_").toIntOrNull() ?: Int.MAX_VALUE
    }
}
