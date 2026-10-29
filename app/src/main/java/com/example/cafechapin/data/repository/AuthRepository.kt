package com.example.cafechapin.data.repository

interface AuthRepository {
    suspend fun ensureSignedIn(): Result<String>
}
