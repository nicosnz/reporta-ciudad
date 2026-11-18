package com.example.reportaciudad.data.remote

import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.MotivoRechazo
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.data.model.ReporteServidor

class RetrofitReporteRemoteDataSource(
    private val api: ReporteApi
) : ReporteRemoteDataSource {

    override suspend fun enviar(reporte: Reporte): Result<String> =
        llamarApi { api.crear(reporte.aDto()) }.mapCatching { creado ->
            creado.id ?: error("El servidor no devolvió un id")
        }

    override suspend fun seguimiento(remoteId: String): Result<SeguimientoReporte> =
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
    override suspend fun listarTodos(): Result<List<ReporteServidor>> =
        llamarApi { api.listar().mapNotNull { it.aServidor() } }

    override suspend fun obtener(remoteId: String): Result<ReporteServidor> =
        llamarApi { api.obtener(remoteId) }.mapCatching { dto ->
            dto.aServidor() ?: error("El servidor no devolvió el reporte")
        }

    override suspend fun actualizar(reporte: ReporteServidor): Result<ReporteServidor> =
        llamarApi { api.actualizar(reporte.remoteId, reporte.aDto()) }.mapCatching { dto ->
            dto.aServidor() ?: error("El servidor no devolvió el reporte")
        }
}
