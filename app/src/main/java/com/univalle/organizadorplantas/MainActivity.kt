// Ubicación MVVM: app/src/main/java/com/univalle/organizadorplantas/MainActivity.kt
package com.univalle.organizadorplantas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.organizadorplantas.data.local.datastore.UserPreferencesRepository
import com.univalle.organizadorplantas.ui.navigation.MainScreen
import com.univalle.organizadorplantas.ui.theme.OrganizadorPlantasTheme
import com.univalle.organizadorplantas.viewmodels.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            // 1. Instanciamos el repositorio de DataStore y el ThemeViewModel
            val preferencesRepository = remember { UserPreferencesRepository(applicationContext) }
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModel.Factory(preferencesRepository)
            )

            // 2. Observamos de forma reactiva el estado del Modo Oscuro desde DataStore
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()

            // 3. Envolvemos toda la app en el tema pasando el booleano reactivo
            OrganizadorPlantasTheme(darkTheme = isDarkTheme) {
                MainScreen()
            }
        }
    }
}
