package com.example.reportaciudad.data.remote

import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.data.model.ReporteServidor
import kotlinx.serialization.Serializable

@Serializable
data class ReporteDto(
    val id: String? = null,
    val emergencia: String?,
    val problema: String?,
    val latitud: Double,
    val longitud: Double,
    val fechaHora: Long,
    val fotoUrl: String?,
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

    estado = EstadoReporte.RECIBIDO.name,
    motivoRechazo = "",
    fotoArregloUrl = ""
)

fun ReporteServidor.aDto(): ReporteDto = ReporteDto(
    id = remoteId,
    emergencia = emergencia?.name,
    problema = problema?.name,
    latitud = latitud,
    longitud = longitud,
    fechaHora = fechaHora,
    fotoUrl = fotoUrl,
    estado = estado.name,
    motivoRechazo = motivoRechazo?.name ?: "",
    fotoArregloUrl = fotoArregloUrl ?: ""
)
