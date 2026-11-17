package com.example.reportaciudad.data.remote

import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.MotivoRechazo
import com.example.reportaciudad.data.model.Reporte
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class RetrofitReporteRemoteDataSource(
    private val api: ReporteApi
) : ReporteRemoteDataSource {

    override suspend fun enviar(reporte: Reporte): Result<String> =
        llamar { api.crear(reporte.aDto()) }.fold(
            onSuccess = { creado ->
                creado.id?.let { Result.success(it) }
                    ?: Result.failure(IllegalStateException("El servidor no devolvió un id"))
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun seguimiento(remoteId: String): Result<SeguimientoReporte> =
        llamar {
            val dto = api.obtener(remoteId)
            SeguimientoReporte(
                estado = EstadoReporte.desdeTexto(dto.estado),
                motivoRechazo = MotivoRechazo.desdeTexto(dto.motivoRechazo),
                fotoArregloUrl = dto.fotoArregloUrl?.takeIf { it.isNotBlank() }
            )
        }

    private inline fun <T> llamar(bloque: () -> T): Result<T> =
        try {
            Result.success(bloque())
        } catch (e: IOException) {          // sin conexión o timeout
            Result.failure(e)
        } catch (e: HttpException) {        // respuesta 4xx / 5xx (404 si lo borraron del servidor)
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
}
