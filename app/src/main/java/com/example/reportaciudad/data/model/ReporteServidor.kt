package com.example.reportaciudad.data.model

import com.example.reportaciudad.util.FechaBolivia

// Un reporte tal como está en MockAPI: lo que ve y cambia el administrador.
// No tiene id de Room, ruta de foto local ni estado de envío: eso solo existe en el teléfono del vecino.
data class ReporteServidor(
    // Id que asignó MockAPI ("1", "2"…): es el que se usa en PUT reportes/{id}
    val remoteId: String,
    // Uno de los dos es null: o es emergencia o es problema de rutina
    val emergencia: TipoEmergencia?,
    val problema: TipoProblema?,
    val latitud: Double,
    val longitud: Double,
    val fechaHora: Long,
    val fotoUrl: String?,
    // Lo que cambia el administrador
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
