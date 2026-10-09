package com.example.cafechapin.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

class ThemePreferences(context: Context) {

    private val dataStore = context.applicationContext.dataStore

    val isDarkTheme: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_THEME] ?: false
    }

    suspend fun setDarkTheme(isDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME] = isDarkTheme
        }
    }

    companion object {
        private val DARK_THEME = booleanPreferencesKey("dark_theme")
    }
}
