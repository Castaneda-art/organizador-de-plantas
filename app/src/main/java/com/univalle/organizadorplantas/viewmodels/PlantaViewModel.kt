package com.univalle.organizadorplantas.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.univalle.organizadorplantas.data.local.room.PlantaEntity
import com.univalle.organizadorplantas.data.repository.PlantaRepository
import com.univalle.organizadorplantas.viewmodels.state.PlantasUiState
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
            // Mapeamos la lista exitosa a nuestro estado Success
            PlantasUiState.Success(lista) as PlantasUiState
        }
        .catch { excepcion ->
            // Si ocurre algún error en la recolección, emitimos el estado Error
            emit(PlantasUiState.Error(excepcion.message ?: "Ocurrió un error inesperado al cargar las plantas"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Optimiza recursos: cancela el flujo si la UI no es visible por 5s
            initialValue = PlantasUiState.Loading // Estado por defecto antes de que Room emita el primer valor
        )

    // 2. Funciones Suspendidas (Corrutinas)
    // Se ejecutan en el viewModelScope para asegurar que operen de manera segura
    // en un hilo secundario y se cancelen si el ViewModel se destruye.
    
    fun agregarPlanta(planta: PlantaEntity) {
        viewModelScope.launch {
            repository.insertarPlanta(planta)
        }
    }

    fun eliminarPlanta(planta: PlantaEntity) {
        viewModelScope.launch {
            repository.eliminarPlanta(planta)
        }
    }

    // 3. Inyección del Repositorio Manual (Factory)
    // Esta Factory permite crear instancias de PlantaViewModel pasándole el PlantaRepository 
    // en el constructor, ya que ViewModelProvider por defecto no sabe cómo inyectar dependencias.
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

/* 
========================================================================================
 GUÍA PARA EL EQUIPO DE FRONTEND: 
 Cómo usar este ViewModel en Jetpack Compose
========================================================================================

Para instanciar e interactuar con este ViewModel desde, por ejemplo, el DashboardScreen:

1. Importar la dependencia necesaria en Compose:
   import androidx.lifecycle.compose.collectAsStateWithLifecycle

2. Instanciar el ViewModel (idealmente pasándolo como parámetro en las rutas de MainScreen):
   val database = AppDatabase.getDatabase(context)
   val repository = PlantaRepository(database.plantaDao())
   val factory = PlantaViewModel.Factory(repository)
   val viewModel: PlantaViewModel = viewModel(factory = factory)

3. Observar el estado en la UI reaccionando al ciclo de vida (¡Mejor práctica!):
   val uiState by viewModel.uiState.collectAsStateWithLifecycle()

4. Actualizar la UI en base al estado:
   when (val state = uiState) {
       is PlantasUiState.Loading -> {
           CircularProgressIndicator() // Mostrar un spinner o esqueleto de carga
       }
       is PlantasUiState.Success -> {
           val misPlantas = state.plantas
           LazyColumn { 
               items(misPlantas) { planta -> 
                   PlantaItemCard(planta) // Dibujar cada planta
               }
           }
       }
       is PlantasUiState.Error -> {
           Text("Error: ${state.message}", color = Color.Red) // Mostrar el error
       }
   }

5. Para interactuar (ej. Borrar una planta al deslizar o hacer clic en un botón):
   viewModel.eliminarPlanta(plantaActual)
========================================================================================
*/
