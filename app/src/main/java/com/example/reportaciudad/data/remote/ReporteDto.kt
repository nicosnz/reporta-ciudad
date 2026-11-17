package com.example.reportaciudad.data.remote

import com.example.reportaciudad.data.model.Reporte
import kotlinx.serialization.Serializable

// JSON del recurso "reportes" en MockAPI. Los enums viajan como texto (BACHE, INCENDIO…).
@Serializable
data class ReporteDto(
    // Lo asigna MockAPI: no se manda al crear (los valores por defecto no se serializan)
    val id: String? = null,
    // Sin valor por defecto: se mandan siempre, aunque sean null
    val emergencia: String?,
    val problema: String?,
    val latitud: Double,
    val longitud: Double,
    // Milisegundos desde 1970 (UTC)
    val fechaHora: Long,
    // Todavía no se suben fotos: queda null hasta tener un servicio de archivos
    val fotoUrl: String?
)

fun Reporte.aDto(): ReporteDto = ReporteDto(
    emergencia = emergencia?.name,
    problema = problema?.name,
    latitud = latitud,
    longitud = longitud,
    fechaHora = fechaHora,
    fotoUrl = null
)
