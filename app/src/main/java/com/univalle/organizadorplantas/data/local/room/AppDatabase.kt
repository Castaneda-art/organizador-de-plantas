package com.univalle.organizadorplantas.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlantaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun plantaDao(): PlantaDao

    companion object {
        // Volatile asegura que los cambios a la variable INSTANCE sean visibles inmediatamente a otros hilos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Devuelve la instancia si ya existe, o crea una nueva si es nula en un bloque sincronizado
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "organizador_plantas_db"
                )
                // .fallbackToDestructiveMigration() // Opcional: útil en la fase inicial de desarrollo si cambias entidades
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}
