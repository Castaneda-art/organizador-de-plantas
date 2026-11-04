package com.univalle.organizadorplantas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.univalle.organizadorplantas.data.local.room.PlantaEntity
import com.univalle.organizadorplantas.viewmodels.PlantaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePlantaScreen(
    plantaId: Int,
    navController: NavController,
    viewModel: PlantaViewModel,
    modifier: Modifier = Modifier
) {
    val plantaState by viewModel.obtenerPlantaPorId(plantaId).collectAsStateWithLifecycle(initialValue = null)

    var mostrarDialogoEdicion by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Planta", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (plantaState != null) {
                        IconButton(onClick = { mostrarDialogoEdicion = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { mostrarDialogoEliminar = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        val planta = plantaState

        if (planta == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Planta no encontrada",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Es posible que haya sido eliminada o que el identificador no sea válido.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Volver al Jardín")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta Principal (Hero Header)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar inicial de la planta
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = planta.nombre.take(1).uppercase(),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = planta.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = planta.especie.ifBlank { "Especie no especificada" },
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        AssistChip(
                            onClick = { },
                            label = { Text("Estado: Activa y Saludable") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }

                // Tarjeta de Cuidado de Riego
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Frecuencia de Riego",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Esta planta requiere riego cada ${planta.diasRiego} ${if (planta.diasRiego == 1) "día" else "días"}.",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "Consejo: Verifica la humedad introduciendo un dedo 2 cm en el sustrato. Riega abundantemente hasta que el agua salga por el drenaje.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        "¡Riego registrado para ${planta.nombre}! Próximo riego en ${planta.diasRiego} días."
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Marcar como regada hoy")
                        }
                    }
                }

                // Tarjeta de Iluminación y Ubicación
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Requerimiento de Luz Solar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Nivel recomendado: ${planta.nivelLuz.ifBlank { "Media" }}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = obtenerConsejoLuz(planta.nivelLuz),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Tarjeta de Guía Botánica y Mantenimiento
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Guía de Mantenimiento",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Temperatura óptima: 18°C a 25°C.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Sustrato: Ligero con buen drenaje (tierra vegetal con perlita).",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Poda: Retira hojas secas o amarillentas para incentivar nuevo follaje.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Acciones al pie
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { mostrarDialogoEdicion = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Editar")
                    }

                    Button(
                        onClick = { mostrarDialogoEliminar = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Eliminar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Diálogo de Edición
    if (mostrarDialogoEdicion && plantaState != null) {
        EditarPlantaDialog(
            planta = plantaState!!,
            onDismiss = { mostrarDialogoEdicion = false },
            onConfirm = { plantaActualizada ->
                viewModel.actualizarPlanta(plantaActualizada)
                mostrarDialogoEdicion = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Planta actualizada correctamente")
                }
            }
        )
    }

    // Diálogo de Confirmación para Eliminar
    if (mostrarDialogoEliminar && plantaState != null) {
        val planta = plantaState!!
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar planta") },
            text = { Text("¿Estás seguro de que deseas eliminar \"${planta.nombre}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarPlanta(planta)
                        mostrarDialogoEliminar = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Retorna recomendación de luz acorde al nivel
 */
fun obtenerConsejoLuz(nivelLuz: String): String {
    return when (nivelLuz.lowercase().trim()) {
        "alta", "directa", "sol directo" ->
            "Requiere luz solar directa o abundante durante al menos 5 a 6 horas al día. Ideal junto a ventanas soleadas o terrazas."
        "baja", "sombra" ->
            "Prospera en lugares con poca luz directa o semisombra. Ideal para baños iluminados, pasillos o interiores de oficina."
        else ->
            "Prefiere luz solar indirecta y brillante. Evita el sol abrasador del mediodía que podría quemar sus hojas."
    }
}
