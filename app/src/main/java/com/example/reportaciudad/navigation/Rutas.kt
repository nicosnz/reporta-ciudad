package com.example.reportaciudad.navigation

import kotlinx.serialization.Serializable

object Ruta {
    @Serializable data object Bienvenida
    @Serializable data object Login
    @Serializable data object Admin
    @Serializable data class AdminDetalle(val remoteId: String)
    @Serializable data class AdminFotoArreglo(val remoteId: String)
    @Serializable data object Inicio
    @Serializable data class Detalle(val id: Long)

    @Serializable data object FlujoReportar
    @Serializable data object PasoTipo
    @Serializable data object PasoEmergencia
    @Serializable data object PasoProblema
    @Serializable data object PasoFotografia
    @Serializable data object PasoLocalizacion
    @Serializable data object PasoResumen
}
