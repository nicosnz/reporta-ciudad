package com.example.reportaciudad.data.remote

import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.MotivoRechazo
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.data.model.ReporteServidor

data class SeguimientoReporte(
    val estado: EstadoReporte,
    val motivoRechazo: MotivoRechazo?,
    val fotoArregloUrl: String?
)

class ReporteRemoteDataSource(
    private val api: ReporteApi
) {

    suspend fun enviar(reporte: Reporte): Result<String> =
        llamarApi { api.crear(reporte.aDto()) }.mapCatching { creado ->
            creado.id ?: error("El servidor no devolvió un id")
        }

    suspend fun seguimiento(remoteId: String): Result<SeguimientoReporte> =
        llamarApi {
            val dto = api.obtener(remoteId)
            SeguimientoReporte(
                estado = EstadoReporte.desdeTexto(dto.estado),
                motivoRechazo = MotivoRechazo.desdeTexto(dto.motivoRechazo),
                fotoArregloUrl = dto.fotoArregloUrl?.trim()?.takeIf { it.startsWith("http") }
            )
        }

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
