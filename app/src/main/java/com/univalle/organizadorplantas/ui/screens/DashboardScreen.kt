package com.univalle.organizadorplantas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.univalle.organizadorplantas.data.local.room.PlantaEntity
import com.univalle.organizadorplantas.ui.navigation.Rutas
import com.univalle.organizadorplantas.viewmodels.PlantaViewModel
import com.univalle.organizadorplantas.viewmodels.state.PlantasUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: PlantaViewModel,
    modifier: Modifier = Modifier
) {
    // 1. Observación reactiva del estado del ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Estados para controlar los diálogos de edición y eliminación
    var plantaAEditar by remember { mutableStateOf<PlantaEntity?>(null) }
    var plantaAEliminar by remember { mutableStateOf<PlantaEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Jardín", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Rutas.AnadirPlanta.ruta) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir planta")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is PlantasUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is PlantasUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error al cargar las plantas",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is PlantasUiState.Success -> {
                    val plantas = state.plantas
                    if (plantas.isEmpty()) {
                        EmptyPlantasView(
                            onAnadirClick = { navController.navigate(Rutas.AnadirPlanta.ruta) }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "Plantas registradas (${plantas.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            items(
                                items = plantas,
                                key = { it.id }
                            ) { planta ->
                                PlantaCard(
                                    planta = planta,
                                    onEdit = { plantaAEditar = planta },
                                    onDelete = { plantaAEliminar = planta }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo para Editar Planta (Update)
    plantaAEditar?.let { planta ->
        EditarPlantaDialog(
            planta = planta,
            onDismiss = { plantaAEditar = null },
            onConfirm = { plantaActualizada ->
                viewModel.actualizarPlanta(plantaActualizada)
                plantaAEditar = null
            }
        )
    }

    // Diálogo para Confirmar Eliminación (Delete)
    plantaAEliminar?.let { planta ->
        AlertDialog(
            onDismissRequest = { plantaAEliminar = null },
            title = { Text("Eliminar planta") },
            text = { Text("¿Deseas eliminar \"${planta.nombre}\" de tu jardín? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarPlanta(planta)
                        plantaAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { plantaAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Tarjeta individual para mostrar los datos de una planta en la lista
 */
@Composable
fun PlantaCard(
    planta: PlantaEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = planta.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = planta.especie.ifBlank { "Sin especie especificada" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar ${planta.nombre}",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar ${planta.nombre}",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "💧 Riego: cada ${planta.diasRiego} días",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "☀️ Luz: ${planta.nivelLuz.ifBlank { "N/A" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Pantalla que se muestra cuando la lista de plantas está vacía
 */
@Composable
fun EmptyPlantasView(
    onAnadirClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🌱",
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tu jardín está vacío",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Añade tus plantas para hacer seguimiento a sus riegos y cuidados.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAnadirClick) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Añadir mi primera planta")
        }
    }
}

/**
 * Diálogo interactivo para editar una planta existente (Update)
 */
@Composable
fun EditarPlantaDialog(
    planta: PlantaEntity,
    onDismiss: () -> Unit,
    onConfirm: (PlantaEntity) -> Unit
) {
    var nombre by remember { mutableStateOf(planta.nombre) }
    var especie by remember { mutableStateOf(planta.especie) }
    var diasRiego by remember { mutableStateOf(planta.diasRiego.toString()) }
    var nivelLuz by remember { mutableStateOf(planta.nivelLuz) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Planta") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = especie,
                    onValueChange = { especie = it },
                    label = { Text("Especie") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = diasRiego,
                    onValueChange = { diasRiego = it },
                    label = { Text("Días de Riego") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nivelLuz,
                    onValueChange = { nivelLuz = it },
                    label = { Text("Nivel de Luz (Alta, Media, Baja)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                enabled = nombre.isNotBlank(),
                onClick = {
                    val diasInt = diasRiego.toIntOrNull() ?: planta.diasRiego
                    val actualizada = planta.copy(
                        nombre = nombre.trim(),
                        especie = especie.trim(),
                        diasRiego = diasInt,
                        nivelLuz = nivelLuz.trim()
                    )
                    onConfirm(actualizada)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
