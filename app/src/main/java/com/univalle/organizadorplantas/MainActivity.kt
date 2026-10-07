package com.univalle.organizadorplantas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// TODO: Importar el tema del proyecto cuando esté generado
// import com.univalle.organizadorplantas.ui.theme.OrganizadorDePlantasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            // OrganizadorDePlantasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home_screen", 
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home_screen") {
                            Text(text = "🏠 Pantalla de Inicio", modifier = Modifier.padding(innerPadding))
                        }
                        composable("add_plant_screen") {
                        }
                        composable("detail_screen/{plantId}") { backStackEntry ->
                        }
                        composable("settings_screen") {
                        }
                    }
                }
            // }
        }
    }
}
