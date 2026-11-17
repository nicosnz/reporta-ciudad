package com.example.reportaciudad.data.remote

import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.MotivoRechazo
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.data.model.ReporteServidor

// Lo que puede cambiar en el servidor después de enviar el reporte
data class SeguimientoReporte(
    val estado: EstadoReporte,
    val motivoRechazo: MotivoRechazo?,
    val fotoArregloUrl: String?
)

// Fuente remota de reportes. El repositorio solo conoce esta interfaz:
// se puede cambiar MockAPI por un servidor real sin tocar nada más.
interface ReporteRemoteDataSource {

    // Devuelve el id que le asignó el servidor
    suspend fun enviar(reporte: Reporte): Result<String>

    // Estado actual del reporte en el servidor
    suspend fun seguimiento(remoteId: String): Result<SeguimientoReporte>

    // Todos los reportes del servidor, de todos los vecinos (para el administrador)
    suspend fun listarTodos(): Result<List<ReporteServidor>>
}
