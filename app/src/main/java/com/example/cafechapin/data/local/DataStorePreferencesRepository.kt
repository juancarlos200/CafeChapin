package com.example.cafechapin.data.local

import com.example.cafechapin.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class DataStorePreferencesRepository(
    private val themePreferences: ThemePreferences
) : PreferencesRepository {

    override fun observeDarkTheme(): Flow<Boolean> = themePreferences.isDarkTheme

    override suspend fun setDarkTheme(isDarkTheme: Boolean): Result<Unit> {
        return runLocal {
            themePreferences.setDarkTheme(isDarkTheme)
        }
    }
}
