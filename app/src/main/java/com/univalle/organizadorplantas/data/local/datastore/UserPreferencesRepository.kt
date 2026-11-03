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

// Extensión de Context para garantizar una única instancia (Singleton) de DataStore en toda la app
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        // Clave tipada para la preferencia del Modo Oscuro
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_enabled")
    }

    // Expone un Flow reactivo que emite el estado actual del Modo Oscuro (false por defecto)
    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }

    // Función suspendida para guardar el estado de forma asíncrona y segura
    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }
}
