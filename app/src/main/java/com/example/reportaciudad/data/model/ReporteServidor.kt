package com.example.reportaciudad.data.model

import com.example.reportaciudad.util.FechaBolivia

data class ReporteServidor(
    val remoteId: String,
    val emergencia: TipoEmergencia?,
    val problema: TipoProblema?,
    val latitud: Double,
    val longitud: Double,
    val fechaHora: Long,
    val fotoUrl: String?,
    val estado: EstadoReporte,
    val motivoRechazo: MotivoRechazo?,
    val fotoArregloUrl: String?
) {
    val categoria: String get() = emergencia?.nombre ?: problema?.nombre.orEmpty()

    val titulo: String
        get() = if (emergencia != null) "Emergencia de ${categoria.lowercase()}"
        else "Reporte de ${categoria.lowercase()}"

    val familia: Familia
        get() = if (emergencia != null) Familia.EMERGENCIA else problema?.familia ?: Familia.URBANO

    val fecha: String get() = FechaBolivia.textoEnvio(fechaHora)
    val recepcion: String get() = "Recibido $fecha"
    val antiguedad: String get() = FechaBolivia.antiguedad(fechaHora)
}
