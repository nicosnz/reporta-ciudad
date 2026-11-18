package com.example.reportaciudad.navigation

import kotlinx.serialization.Serializable

// Rutas type-safe: el compilador verifica destinos y argumentos (nada de strings sueltos)
object Ruta {
    @Serializable data object Bienvenida
    @Serializable data object Login
    @Serializable data object Admin
    @Serializable data class AdminDetalle(val remoteId: String)
    @Serializable data class AdminFotoArreglo(val remoteId: String)
    @Serializable data object Inicio
    @Serializable data class Detalle(val id: Long)

    // Subgrafo del flujo "Reportar": sus pantallas comparten un mismo ReporteFormularioViewModel
    @Serializable data object FlujoReportar
    @Serializable data object PasoTipo
    @Serializable data object PasoEmergencia
    @Serializable data object PasoProblema
    @Serializable data object PasoFotografia
    @Serializable data object PasoLocalizacion
    @Serializable data object PasoResumen
}
