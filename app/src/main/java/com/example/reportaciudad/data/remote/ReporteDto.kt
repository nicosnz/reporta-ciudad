package com.example.reportaciudad.data.remote

import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.Reporte
import kotlinx.serialization.Serializable

// JSON del recurso "reportes" en MockAPI. Los enums viajan como texto (BACHE, INCENDIO, EN_REVISION…).
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
    // URL de la foto en Cloudinary; null si el reporte se envió sin foto
    val fotoUrl: String?,
    // Los cambia el administrador. Un reporte viejo puede no tenerlos: se leen como RECIBIDO y vacío.
    val estado: String? = null,
    val motivoRechazo: String? = null,
    val fotoArregloUrl: String? = null
)

fun Reporte.aDto(): ReporteDto = ReporteDto(
    emergencia = emergencia?.name,
    problema = problema?.name,
    latitud = latitud,
    longitud = longitud,
    fechaHora = fechaHora,
    fotoUrl = fotoUrl,
    // Todo reporte nuevo empieza como recibido
    estado = EstadoReporte.RECIBIDO.name
)
