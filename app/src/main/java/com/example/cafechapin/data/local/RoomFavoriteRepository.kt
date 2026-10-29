package com.example.cafechapin.data.local

import com.example.cafechapin.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomFavoriteRepository(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun observeFavoriteIds(): Flow<Set<String>> {
        return favoriteDao.getFavorites().map { favorites ->
            favorites.map { it.productId }.toSet()
        }
    }

    override suspend fun setFavorite(productId: String, isFavorite: Boolean): Result<Unit> {
        return runLocal {
            val favorite = FavoriteEntity(productId = productId)

            if (isFavorite) {
                favoriteDao.insert(favorite)
            } else {
                favoriteDao.delete(favorite)
            }
        }
    }
}
