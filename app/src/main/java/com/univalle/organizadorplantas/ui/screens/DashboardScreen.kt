package com.univalle.organizadorplantas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Estados locales para búsqueda, edición y eliminación
    var filtroBusqueda by remember { mutableStateOf("") }
    var plantaAEditar by remember { mutableStateOf<PlantaEntity?>(null) }
    var plantaAEliminar by remember { mutableStateOf<PlantaEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌿", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mi Jardín", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Rutas.AnadirPlanta.ruta) },
                icon = { Icon(Icons.Default.Add, contentDescription = "Añadir planta") },
                text = { Text("Añadir Planta") }
            )
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
                    val todasLasPlantas = state.plantas

                    if (todasLasPlantas.isEmpty()) {
                        EmptyPlantasView(
                            onAnadirClick = { navController.navigate(Rutas.AnadirPlanta.ruta) },
                            onCargarEjemplosClick = { viewModel.cargarPlantasDeEjemplo() }
                        )
                    } else {
                        // Filtrado por buscador
                        val plantasFiltradas = remember(todasLasPlantas, filtroBusqueda) {
                            if (filtroBusqueda.isBlank()) {
                                todasLasPlantas
                            } else {
                                todasLasPlantas.filter {
                                    it.nombre.contains(filtroBusqueda, ignoreCase = true) ||
                                            it.especie.contains(filtroBusqueda, ignoreCase = true)
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Cabecera con Resumen del Jardín
                            item {
                                ResumenJardinCard(
                                    totalPlantas = todasLasPlantas.size
                                )
                            }

                            // Barra de búsqueda reactiva
                            item {
                                OutlinedTextField(
                                    value = filtroBusqueda,
                                    onValueChange = { filtroBusqueda = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    placeholder = { Text("Buscar planta por nombre o especie...") },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                                    trailingIcon = {
                                        if (filtroBusqueda.isNotEmpty()) {
                                            IconButton(onClick = { filtroBusqueda = "" }) {
                                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }

                            // Título de la lista
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (filtroBusqueda.isBlank()) {
                                            "Mis Plantas (${plantasFiltradas.size})"
                                        } else {
                                            "Resultados (${plantasFiltradas.size})"
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "Toca para ver detalles",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Lista de tarjetas visuales de plantas
                            items(
                                items = plantasFiltradas,
                                key = { it.id }
                            ) { planta ->
                                PlantaVisualCard(
                                    planta = planta,
                                    onClick = {
                                        navController.navigate(Rutas.DetallePlanta.crearRuta(planta.id))
                                    },
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
 * Tarjeta de Resumen con Métricas del Jardín
 */
@Composable
fun ResumenJardinCard(
    totalPlantas: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "🌱 Estado del Jardín",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (totalPlantas == 1) "1 planta registrada y cuidada" else "$totalPlantas plantas registradas y cuidadas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = totalPlantas.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta visual interactiva para mostrar los datos de una planta en la lista
 */
@Composable
fun PlantaVisualCard(
    planta: PlantaEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar botánico
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = obtenerEmojiPlanta(planta.nombre, planta.especie),
                        fontSize = 26.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Nombre y especie
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = planta.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = planta.especie.ifBlank { "Sin especie especificada" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Acciones rápidas
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar ${planta.nombre}",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar ${planta.nombre}",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Chips con información visual de cuidados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = onClick,
                    label = { Text("💧 Cada ${planta.diasRiego} días") }
                )
                AssistChip(
                    onClick = onClick,
                    label = { Text("☀️ Luz ${planta.nivelLuz.ifBlank { "Media" }}") }
                )
            }
        }
    }
}

/**
 * Pantalla amigable que se muestra cuando la lista de plantas está vacía
 */
@Composable
fun EmptyPlantasView(
    onAnadirClick: () -> Unit,
    onCargarEjemplosClick: () -> Unit,
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
            text = "🪴",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tu jardín está vacío",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Añade tus plantas para hacer seguimiento a sus riegos, cuidados y salud botánica.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAnadirClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Añadir mi primera planta")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onCargarEjemplosClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🌱 Cargar plantas de ejemplo")
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
