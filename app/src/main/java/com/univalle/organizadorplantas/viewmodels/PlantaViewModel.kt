package com.univalle.organizadorplantas.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.univalle.organizadorplantas.data.local.room.PlantaEntity
import com.univalle.organizadorplantas.data.repository.PlantaRepository
import com.univalle.organizadorplantas.viewmodels.state.PlantasUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantaViewModel(private val repository: PlantaRepository) : ViewModel() {

    // 1. Estado Reactivo (StateFlow)
    // Transformamos el Flow<List<PlantaEntity>> del repositorio en nuestro PlantasUiState, 
    // y luego lo convertimos en un StateFlow con stateIn().
    val uiState: StateFlow<PlantasUiState> = repository.todasLasPlantas
        .map { lista ->
            PlantasUiState.Success(lista) as PlantasUiState
        }
        .catch { excepcion ->
            emit(PlantasUiState.Error(excepcion.message ?: "Ocurrió un error inesperado al cargar las plantas"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlantasUiState.Loading
        )

    // 2. Funciones Suspendidas (Corrutinas)
    fun agregarPlanta(planta: PlantaEntity) {
        viewModelScope.launch {
            repository.insertarPlanta(planta)
        }
    }

    fun actualizarPlanta(planta: PlantaEntity) {
        viewModelScope.launch {
            repository.actualizarPlanta(planta)
        }
    }

    fun eliminarPlanta(planta: PlantaEntity) {
        viewModelScope.launch {
            repository.eliminarPlanta(planta)
        }
    }

    fun obtenerPlantaPorId(id: Int): Flow<PlantaEntity?> {
        return repository.obtenerPlantaPorId(id)
    }

    fun cargarPlantasDeEjemplo() {
        viewModelScope.launch {
            val ejemplos = listOf(
                PlantaEntity(
                    nombre = "Monstera Deliciosa",
                    especie = "Monstera deliciosa",
                    diasRiego = 7,
                    nivelLuz = "Media"
                ),
                PlantaEntity(
                    nombre = "Lengua de Suegra",
                    especie = "Sansevieria trifasciata",
                    diasRiego = 14,
                    nivelLuz = "Baja"
                ),
                PlantaEntity(
                    nombre = "Pothos Dorado",
                    especie = "Epipremnum aureum",
                    diasRiego = 4,
                    nivelLuz = "Media"
                ),
                PlantaEntity(
                    nombre = "Suculenta Echeveria",
                    especie = "Echeveria elegans",
                    diasRiego = 10,
                    nivelLuz = "Alta"
                ),
                PlantaEntity(
                    nombre = "Ficus Lyrata",
                    especie = "Ficus lyrata",
                    diasRiego = 5,
                    nivelLuz = "Alta"
                )
            )
            ejemplos.forEach { repository.insertarPlanta(it) }
        }
    }

    // 3. Inyección del Repositorio Manual (Factory)
    class Factory(private val repository: PlantaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlantaViewModel::class.java)) {
                return PlantaViewModel(repository) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}
