package com.example.cafechapin.data.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun observeDarkTheme(): Flow<Boolean>
    suspend fun setDarkTheme(isDarkTheme: Boolean): Result<Unit>
}
