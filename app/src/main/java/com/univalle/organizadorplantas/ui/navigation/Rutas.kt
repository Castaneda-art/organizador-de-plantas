package com.univalle.organizadorplantas.ui.navigation

sealed class Rutas(val ruta: String) {
    object Splash : Rutas("splash")
    object Auth : Rutas("auth")
    object Dashboard : Rutas("dashboard")
    object AnadirPlanta : Rutas("anadir_planta")
    object Alertas : Rutas("alertas")
    object Diagnostico : Rutas("diagnostico")
    
    // Ruta dinámica que requiere un argumento
    object DetallePlanta : Rutas("detalle_planta/{plantaId}") {
        fun crearRuta(plantaId: Int): String {
            return "detalle_planta/$plantaId"
        }
    }
}
