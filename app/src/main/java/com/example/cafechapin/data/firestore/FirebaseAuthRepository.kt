package com.example.cafechapin.data.firestore

import com.example.cafechapin.data.repository.AuthRepository
import com.example.cafechapin.model.StoreError
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth
) : AuthRepository {

    override suspend fun ensureSignedIn(): Result<String> {
        return try {
            val user = auth.currentUser ?: auth.signInAnonymously().await().user

            if (user == null) {
                Result.failure(StoreError.PermissionDenied())
            } else {
                Result.success(user.uid)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toStoreError())
        }
    }
}
