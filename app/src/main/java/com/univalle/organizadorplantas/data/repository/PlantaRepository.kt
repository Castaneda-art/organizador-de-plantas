package com.univalle.organizadorplantas.data.repository

import com.univalle.organizadorplantas.data.local.room.PlantaDao
import com.univalle.organizadorplantas.data.local.room.PlantaEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que sirve como la única fuente de verdad para los datos de las Plantas.
 * Encapsula la lógica de acceso a datos (DAO) y expone la información al ViewModel.
 */
class PlantaRepository(private val plantaDao: PlantaDao) {

    val todasLasPlantas: Flow<List<PlantaEntity>> = plantaDao.obtenerTodasLasPlantas()

    fun obtenerPlantaPorId(id: Int): Flow<PlantaEntity?> {
        return plantaDao.obtenerPlantaPorId(id)
    }

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
