package com.univalle.organizadorplantas.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// Pantallas Placeholders temporales para que el equipo de UI comience a trabajar

@Composable fun SplashScreen() = PlaceholderScreen("Splash Screen")
@Composable fun AuthScreen() = PlaceholderScreen("Auth Screen (Login / Registro)")
@Composable fun DashboardScreen() = PlaceholderScreen("Dashboard (Mi Jardín)")
@Composable fun AnadirPlantaScreen() = PlaceholderScreen("Añadir Planta")
@Composable fun AlertasScreen() = PlaceholderScreen("Alertas")
@Composable fun DiagnosticoScreen() = PlaceholderScreen("Diagnóstico")

@Composable 
fun DetallePlantaScreen(plantaId: String?) {
    PlaceholderScreen("Detalle de Planta ID: $plantaId")
}

// Componente helper para centrar el texto
@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}
