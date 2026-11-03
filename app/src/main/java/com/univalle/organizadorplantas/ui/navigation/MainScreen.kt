package com.univalle.organizadorplantas.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
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
import com.univalle.organizadorplantas.data.local.datastore.UserPreferencesRepository
import com.univalle.organizadorplantas.data.local.room.AppDatabase
import com.univalle.organizadorplantas.data.repository.PlantaRepository
import com.univalle.organizadorplantas.ui.screens.*
import com.univalle.organizadorplantas.viewmodels.PlantaViewModel
import com.univalle.organizadorplantas.viewmodels.ThemeViewModel

@Composable
fun MainScreen(
    themeViewModel: ThemeViewModel? = null
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Inyección de dependencias
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { PlantaRepository(database.plantaDao()) }
    val factory = remember { PlantaViewModel.Factory(repository) }
    val plantaViewModel: PlantaViewModel = viewModel(factory = factory)

    // Instancia de ThemeViewModel para Ajustes si no viene inyectada
    val actualThemeViewModel: ThemeViewModel = themeViewModel ?: viewModel(
        factory = ThemeViewModel.Factory(remember { UserPreferencesRepository(context.applicationContext) })
    )

    // Rutas que muestran la barra inferior de navegación
    val rutasConBottomBar = listOf(
        Rutas.Dashboard.ruta,
        Rutas.Alertas.ruta,
        Rutas.AnadirPlanta.ruta,
        Rutas.Diagnostico.ruta,
        Rutas.Ajustes.ruta
    )

    val showBottomBar = currentRoute in rutasConBottomBar

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Jardín") },
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
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                        label = { Text("Ajustes") },
                        selected = currentRoute == Rutas.Ajustes.ruta,
                        onClick = {
                            navController.navigate(Rutas.Ajustes.ruta) {
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
            startDestination = Rutas.Splash.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rutas.Splash.ruta) { SplashScreen(navController) }
            composable(Rutas.Auth.ruta) { AuthScreen(navController) }

            composable(Rutas.Dashboard.ruta) {
                DashboardScreen(
                    navController = navController,
                    viewModel = plantaViewModel
                )
            }

            composable(Rutas.AnadirPlanta.ruta) {
                AnadirPlantaScreen(
                    navController = navController,
                    viewModel = plantaViewModel
                )
            }

            composable(Rutas.Alertas.ruta) {
                AlertasScreen(
                    navController = navController,
                    viewModel = plantaViewModel
                )
            }

            composable(Rutas.Diagnostico.ruta) {
                DiagnosticoScreen(navController = navController)
            }

            composable(Rutas.Ajustes.ruta) {
                AjustesScreen(themeViewModel = actualThemeViewModel)
            }

            composable(
                route = Rutas.DetallePlanta.ruta,
                arguments = listOf(navArgument("plantaId") { type = NavType.IntType })
            ) { backStackEntry ->
                val plantaId = backStackEntry.arguments?.getInt("plantaId") ?: 0
                DetallePlantaScreen(
                    plantaId = plantaId,
                    navController = navController,
                    viewModel = plantaViewModel
                )
            }
        }
    }
}
