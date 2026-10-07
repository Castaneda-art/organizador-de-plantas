package com.univalle.organizadorplantas.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.univalle.organizadorplantas.data.local.room.AppDatabase
import com.univalle.organizadorplantas.data.repository.PlantaRepository
import com.univalle.organizadorplantas.ui.screens.*
import com.univalle.organizadorplantas.viewmodels.PlantaViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 1. Inyección manual de dependencias
    // Obtenemos el contexto actual para inicializar la base de datos
    val context = LocalContext.current
    
    // remember garantiza que no estemos recreando las instancias en cada recomposición de la UI
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { PlantaRepository(database.plantaDao()) }
    val factory = remember { PlantaViewModel.Factory(repository) }
    
    // Obtenemos la instancia unificada de PlantaViewModel atada al ciclo de vida
    val plantaViewModel: PlantaViewModel = viewModel(factory = factory)

    // Definimos qué rutas deben mostrar la barra inferior
    val rutasConBottomBar = listOf(
        Rutas.Dashboard.ruta,
        Rutas.AnadirPlanta.ruta,
        Rutas.Alertas.ruta,
        Rutas.Diagnostico.ruta
    )

    val showBottomBar = currentRoute in rutasConBottomBar

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                        label = { Text("Jardín") },
                        selected = currentRoute == Rutas.Dashboard.ruta,
                        onClick = {
                            navController.navigate(Rutas.Dashboard.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Add, contentDescription = "Añadir") },
                        label = { Text("Añadir") },
                        selected = currentRoute == Rutas.AnadirPlanta.ruta,
                        onClick = {
                            navController.navigate(Rutas.AnadirPlanta.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Notifications, contentDescription = "Alertas") },
                        label = { Text("Alertas") },
                        selected = currentRoute == Rutas.Alertas.ruta,
                        onClick = {
                            navController.navigate(Rutas.Alertas.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Build, contentDescription = "Diagnóstico") },
                        label = { Text("Diagnóstico") },
                        selected = currentRoute == Rutas.Diagnostico.ruta,
                        onClick = {
                            navController.navigate(Rutas.Diagnostico.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.Splash.ruta, // La app iniciará en el Splash Screen
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rutas.Splash.ruta) { SplashScreen() }
            composable(Rutas.Auth.ruta) { AuthScreen() }
            
            // TODO: Cuando el equipo termine el DashboardScreen, también podrán pasarle el 'plantaViewModel' aquí
            composable(Rutas.Dashboard.ruta) { DashboardScreen() }
            
            // 2. Aquí inyectamos el navController y plantaViewModel en la vista creada
            composable(Rutas.AnadirPlanta.ruta) { 
                AnadirPlantaScreen(
                    navController = navController, 
                    viewModel = plantaViewModel
                ) 
            }
            
            composable(Rutas.Alertas.ruta) { AlertasScreen() }
            composable(Rutas.Diagnostico.ruta) { DiagnosticoScreen() }
            
            composable(
                route = Rutas.DetallePlanta.ruta,
                arguments = listOf(navArgument("plantaId") { type = NavType.IntType })
            ) { backStackEntry ->
                val plantaId = backStackEntry.arguments?.getInt("plantaId")?.toString()
                DetallePlantaScreen(plantaId = plantaId)
            }
        }
    }
}
