package com.univalle.organizadorplantas.data.repository

import com.univalle.organizadorplantas.data.local.room.PlantaDao
import com.univalle.organizadorplantas.data.local.room.PlantaEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que sirve como la única fuente de verdad para los datos de las Plantas.
 * Encapsula la lógica de acceso a datos (DAO) y expone la información al ViewModel.
 */
class PlantaRepository(private val plantaDao: PlantaDao) {

    // TODO (Equipo): En el ViewModel, usar .stateIn(viewModelScope, ...) para convertir 
    // este Flow en un StateFlow que la UI en Compose pueda observar con collectAsStateWithLifecycle().
    val todasLasPlantas: Flow<List<PlantaEntity>> = plantaDao.obtenerTodasLasPlantas()

    // TODO (Equipo): Al llamar estas funciones desde el ViewModel, asegúrense de usar 
    // viewModelScope.launch { repository.insertarPlanta(planta) }
    suspend fun insertarPlanta(planta: PlantaEntity) {
        plantaDao.insertarPlanta(planta)
    }

    suspend fun actualizarPlanta(planta: PlantaEntity) {
        plantaDao.actualizarPlanta(planta)
    }

    suspend fun eliminarPlanta(planta: PlantaEntity) {
        plantaDao.eliminarPlanta(planta)
    }
}
