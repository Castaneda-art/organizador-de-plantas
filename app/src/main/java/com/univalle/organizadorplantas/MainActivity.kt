package com.univalle.organizadorplantas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.univalle.organizadorplantas.ui.navigation.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            // TODO: Envolver con OrganizadorDePlantasTheme { ... } cuando se genere la carpeta theme
            MainScreen()
        }
    }
}
