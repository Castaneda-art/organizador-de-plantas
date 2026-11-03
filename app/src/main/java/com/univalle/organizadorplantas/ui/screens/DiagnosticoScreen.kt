package com.univalle.organizadorplantas.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class SintomaDiagnostico(
    val id: Int,
    val emoji: String,
    val sintoma: String,
    val causaComun: String,
    val solucionRecomendada: String,
    val prevencion: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticoScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val sintomas = remember {
        listOf(
            SintomaDiagnostico(
                id = 1,
                emoji = "🍂",
                sintoma = "Hojas amarillas y blandas",
                causaComun = "Exceso de riego y falta de oxigenación en las raíces (encharcamiento).",
                solucionRecomendada = "Suspende el riego inmediatamente hasta que el sustrato se seque por completo. Revisa que el orificio de drenaje de la maceta no esté bloqueado.",
                prevencion = "Asegura sustrato con perlita y maceta con orificios."
            ),
            SintomaDiagnostico(
                id = 2,
                emoji = "🥀",
                sintoma = "Hojas marchitas o caídas",
                causaComun = "Falta de agua o estrés por altas temperaturas.",
                solucionRecomendada = "Aplica un riego profundo por inmersión o riego lento hasta empapar el sustrato. Mueve la planta a un sitio fresco y sombreado mientras se recupera.",
                prevencion = "Mantén un calendario de riego constante."
            ),
            SintomaDiagnostico(
                id = 3,
                emoji = "🟤",
                sintoma = "Puntas secas o bordes marrones",
                causaComun = "Baja humedad ambiental o acumulación de sales de fertilizante.",
                solucionRecomendada = "Pulveriza agua en las hojas (evitando flores) o coloca un plato con guijarros húmedos debajo de la maceta.",
                prevencion = "Evita colocar las plantas cerca de corrientes de aire o aires acondicionados."
            ),
            SintomaDiagnostico(
                id = 4,
                emoji = "⚪",
                sintoma = "Polvillo blanco o telarañas finas",
                causaComun = "Presencia de hongos (oídio) o plaga de ácaros / araña roja.",
                solucionRecomendada = "Limpia las hojas con un paño húmedo y solución de jabón potásico o aceite de neem. Aísla la planta de las demás.",
                prevencion = "Mantén buena ventilación e inspecciona periódicamente el envés de las hojas."
            ),
            SintomaDiagnostico(
                id = 5,
                emoji = "🌱",
                sintoma = "Crecimiento lento y tallos débiles (ahilamiento)",
                causaComun = "Falta de luz solar suficiente (la planta se estira buscando sol).",
                solucionRecomendada = "Traslada la planta gradualmente a una ubicación con mayor luz solar indirecta.",
                prevencion = "Conoce las necesidades de luz específicas de cada especie."
            )
        )
    }

    var sintomaSeleccionadoId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🩺", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Diagnóstico Botánico", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Asistente de Salud Botánica",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = "Selecciona el síntoma visible en tu planta para ver la causa y solución recomendada.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Síntomas frecuentes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            items(sintomas, key = { it.id }) { item ->
                val expandido = sintomaSeleccionadoId == item.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            sintomaSeleccionadoId = if (expandido) null else item.id
                        },
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = item.emoji, fontSize = 28.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.sintoma,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (expandido) "Toca para colapsar" else "Toca para ver causas y solución",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        AnimatedVisibility(visible = expandido) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                Text(
                                    text = "🔍 Causa probable:",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = item.causaComun,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "💡 Solución recomendada:",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = item.solucionRecomendada,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "🛡️ Prevención:",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = item.prevencion,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
