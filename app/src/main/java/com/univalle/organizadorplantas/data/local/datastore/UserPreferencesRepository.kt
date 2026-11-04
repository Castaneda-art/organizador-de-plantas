// Ubicación MVVM: app/src/main/java/com/univalle/organizadorplantas/data/local/datastore/UserPreferencesRepository.kt
package com.univalle.organizadorplantas.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_enabled")
        val WATERING_REMINDERS_KEY = booleanPreferencesKey("watering_reminders_enabled")
        val SEASONAL_TIPS_KEY = booleanPreferencesKey("seasonal_tips_enabled")
    }

    // Preferencias observables. Los valores por defecto coinciden con los de Ajustes.
    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[DARK_THEME_KEY] ?: false }

    val areWateringRemindersEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[WATERING_REMINDERS_KEY] ?: true }

    val areSeasonalTipsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[SEASONAL_TIPS_KEY] ?: false }

    // Escrituras persistentes y asíncronas en DataStore.
    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }

    suspend fun setWateringRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[WATERING_REMINDERS_KEY] = enabled
        }
    }

    suspend fun setSeasonalTipsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SEASONAL_TIPS_KEY] = enabled
        }
    }
}
