// Ubicación MVVM: app/src/main/java/com/univalle/organizadorplantas/ui/screens/AjustesScreen.kt
package com.univalle.organizadorplantas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.univalle.organizadorplantas.viewmodels.ThemeViewModel

@Composable
fun AjustesScreen(
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    // Observa el estado del tema de forma segura con respecto al ciclo de vida
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineSmall
        )

        DarkThemePreferenceCard(
            isDarkTheme = isDarkTheme,
            onThemeChanged = { enabled ->
                themeViewModel.toggleDarkTheme(enabled)
            }
        )
    }
}

/**
 * Componente reutilizable que también puede incrustarse en el Dashboard o AlertasScreen.
 */
@Composable
fun DarkThemePreferenceCard(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Modo Oscuro",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Activa el tema oscuro para descansar la vista",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = isDarkTheme,
                onCheckedChange = { isChecked ->
                    onThemeChanged(isChecked)
                }
            )
        }
    }
}
