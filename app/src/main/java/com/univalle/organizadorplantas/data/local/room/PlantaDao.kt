package com.univalle.organizadorplantas.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPlanta(planta: PlantaEntity)

    // Flow ya es reactivo por naturaleza en Room y corre asíncronamente
    @Query("SELECT * FROM plantas ORDER BY nombre ASC")
    fun obtenerTodasLasPlantas(): Flow<List<PlantaEntity>>

    @Update
    suspend fun actualizarPlanta(planta: PlantaEntity)

    @Delete
    suspend fun eliminarPlanta(planta: PlantaEntity)
}
