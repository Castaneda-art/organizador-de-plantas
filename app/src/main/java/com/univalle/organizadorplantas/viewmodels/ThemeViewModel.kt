// Ubicación MVVM: app/src/main/java/com/univalle/organizadorplantas/viewmodels/ThemeViewModel.kt
package com.univalle.organizadorplantas.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.univalle.organizadorplantas.data.local.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel que expone y actualiza las preferencias de usuario almacenadas en DataStore.
 * Se conserva el nombre ThemeViewModel para no romper las referencias existentes del proyecto.
 */
class ThemeViewModel(private val preferencesRepository: UserPreferencesRepository) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = preferencesRepository.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val areWateringRemindersEnabled: StateFlow<Boolean> = preferencesRepository.areWateringRemindersEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    val areSeasonalTipsEnabled: StateFlow<Boolean> = preferencesRepository.areSeasonalTipsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun toggleDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDarkTheme(enabled)
        }
    }

    fun setWateringRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setWateringRemindersEnabled(enabled)
        }
    }

    fun setSeasonalTipsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setSeasonalTipsEnabled(enabled)
        }
    }

    // Factory manual para inyectar UserPreferencesRepository sin requerir Hilt/Dagger.
    class Factory(private val repository: UserPreferencesRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
                return ThemeViewModel(repository) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}
