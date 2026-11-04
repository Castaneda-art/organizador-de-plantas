package com.univalle.organizadorplantas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    // 1. Manejo de Estado del Formulario
    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var diasRiego by remember { mutableStateOf("") }
    var nivelLuz by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val opcionesLuz = listOf("Alta", "Media", "Baja")
    val opcionesDias = listOf("1", "3", "5", "7", "14")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Añadir Nueva Planta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre común de la planta *") },
                placeholder = { Text("Ej: Monstera Deliciosa, Pothos, Orquídea...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = especie,
                onValueChange = { especie = it },
                label = { Text("Especie o nombre científico") },
                placeholder = { Text("Ej: Monstera deliciosa, Epipremnum...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = diasRiego,
                    onValueChange = { diasRiego = it },
                    label = { Text("Frecuencia de Riego (cada cuántos días)") },
                    placeholder = { Text("Ej: 7") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    opcionesDias.forEach { d ->
                        FilterChip(
                            selected = diasRiego == d,
                            onClick = { diasRiego = d },
                            label = { Text("$d ${if (d == "1") "día" else "días"}") }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = nivelLuz,
                    onValueChange = { nivelLuz = it },
                    label = { Text("Nivel de Luz solar") },
                    placeholder = { Text("Alta, Media o Baja") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    opcionesLuz.forEach { luz ->
                        FilterChip(
                            selected = nivelLuz.equals(luz, ignoreCase = true),
                            onClick = { nivelLuz = luz },
                            label = { Text("Luz $luz") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = nombre.isNotBlank(),
                onClick = {
                    val diasInt = diasRiego.toIntOrNull() ?: 1
                    val luzFinal = if (nivelLuz.isBlank()) "Media" else nivelLuz.trim()

                    val nuevaPlanta = PlantaEntity(
                        nombre = nombre.trim(),
                        especie = especie.trim(),
                        diasRiego = diasInt,
                        nivelLuz = luzFinal,
                        imagenUri = null
                    )

                    viewModel.agregarPlanta(nuevaPlanta)

                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "¡${nuevaPlanta.nombre} añadida a tu jardín!",
                            duration = SnackbarDuration.Short
                        )
                        navController.popBackStack()
                    }
                }
            ) {
                Text("Guardar Planta en Mi Jardín")
            }
        }
    }
}
