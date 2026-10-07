package com.univalle.organizadorplantas.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plantas")
data class PlantaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val especie: String,
    val diasRiego: Int, // Representa cada cuántos días se debe regar
    val nivelLuz: String,
    val imagenUri: String? = null // Opcional/nullable para la ruta de la foto
)
