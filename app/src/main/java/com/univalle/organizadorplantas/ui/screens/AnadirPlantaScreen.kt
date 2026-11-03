package com.univalle.organizadorplantas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.univalle.organizadorplantas.data.local.room.PlantaEntity
import com.univalle.organizadorplantas.viewmodels.PlantaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnadirPlantaScreen(
    navController: NavController,
    viewModel: PlantaViewModel
) {
    // 1. Manejo de Estado (State) del Formulario
    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var diasRiego by remember { mutableStateOf("") }
    var nivelLuz by remember { mutableStateOf("") }

    // Control para mostrar el Snackbar y navegar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 2. Estructura Visual (Scaffold y TopAppBar)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Añadir Planta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        // 3. Formulario (Column con OutlinedTextFields)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la planta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = especie,
                onValueChange = { especie = it },
                label = { Text("Especie") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = diasRiego,
                onValueChange = { diasRiego = it },
                label = { Text("Días de Riego") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Usamos un TextField normal por ahora como solicitaste. 
            // El equipo puede migrarlo a DropdownMenuItem (ExposedDropdownMenuBox) después.
            OutlinedTextField(
                value = nivelLuz,
                onValueChange = { nivelLuz = it },
                label = { Text("Nivel de Luz (Alta, Media, Baja)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia la parte inferior si hay espacio

            // 4. Botón de Guardar y Feedback Visual
            Button(
                modifier = Modifier.fillMaxWidth(),
                // Previene guardar si el nombre está vacío
                enabled = nombre.isNotBlank(),
                onClick = {
                    // Conversión segura para evitar crasheos si se ingresa texto no numérico accidentalmente
                    val diasInt = diasRiego.toIntOrNull() ?: 1

                    val nuevaPlanta = PlantaEntity(
                        nombre = nombre,
                        especie = especie,
                        diasRiego = diasInt,
                        nivelLuz = nivelLuz,
                        imagenUri = null // Opcional por el momento
                    )

                    // Guardamos la planta a través del ViewModel (Room)
                    viewModel.agregarPlanta(nuevaPlanta)

                    // Mostramos el Snackbar y regresamos al Dashboard
                    coroutineScope.launch {
                        // El snackbar pausa la corrutina brevemente para mostrar el mensaje
                        snackbarHostState.showSnackbar(
                            message = "Planta añadida correctamente",
                            duration = SnackbarDuration.Short
                        )
                        // popBackStack devuelve al usuario a la pantalla anterior (Dashboard)
                        navController.popBackStack()
                    }
                }
            ) {
                Text("Guardar Planta")
            }
        }
    }
}
