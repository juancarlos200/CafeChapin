package com.example.cafechapin.data.repository

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavoriteIds(): Flow<Set<String>>
    suspend fun setFavorite(productId: String, isFavorite: Boolean): Result<Unit>
}
