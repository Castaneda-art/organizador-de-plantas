package com.univalle.organizadorplantas.viewmodels.state

import com.univalle.organizadorplantas.data.local.room.PlantaEntity

/**
 * Representa los distintos estados por los que puede pasar la interfaz 
 * al interactuar con los datos de las plantas.
 */
sealed interface PlantasUiState {
    object Loading : PlantasUiState
    data class Success(val plantas: List<PlantaEntity>) : PlantasUiState
    data class Error(val message: String) : PlantasUiState
}
