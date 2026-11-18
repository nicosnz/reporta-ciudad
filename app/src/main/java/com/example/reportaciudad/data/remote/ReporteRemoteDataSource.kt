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

// Reportes en el servidor (MockAPI). Para cambiar de servidor solo hay que tocar esta clase y ReporteApi.
class ReporteRemoteDataSource(
    private val api: ReporteApi
) {

    // Devuelve el id que le asignó el servidor
    suspend fun enviar(reporte: Reporte): Result<String> =
        llamarApi { api.crear(reporte.aDto()) }.mapCatching { creado ->
            creado.id ?: error("El servidor no devolvió un id")
        }

    // Estado actual del reporte en el servidor
    suspend fun seguimiento(remoteId: String): Result<SeguimientoReporte> =
        llamarApi {
            val dto = api.obtener(remoteId)
            SeguimientoReporte(
                estado = EstadoReporte.desdeTexto(dto.estado),
                motivoRechazo = MotivoRechazo.desdeTexto(dto.motivoRechazo),
                // Solo una URL real: descarta vacíos y textos de relleno de MockAPI
                fotoArregloUrl = dto.fotoArregloUrl?.trim()?.takeIf { it.startsWith("http") }
            )
        }

    // Los que vienen sin id se descartan (aServidor devuelve null)
    suspend fun listarTodos(): Result<List<ReporteServidor>> =
        llamarApi { api.listar().mapNotNull { it.aServidor() } }

    suspend fun obtener(remoteId: String): Result<ReporteServidor> =
        llamarApi { api.obtener(remoteId) }.mapCatching { dto ->
            dto.aServidor() ?: error("El servidor no devolvió el reporte")
        }

    suspend fun actualizar(reporte: ReporteServidor): Result<ReporteServidor> =
        llamarApi { api.actualizar(reporte.remoteId, reporte.aDto()) }.mapCatching { dto ->
            dto.aServidor() ?: error("El servidor no devolvió el reporte")
        }
}
