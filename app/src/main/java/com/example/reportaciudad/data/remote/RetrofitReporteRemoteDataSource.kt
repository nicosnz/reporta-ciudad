package com.example.reportaciudad.data.remote

import com.example.reportaciudad.data.model.Reporte
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class RetrofitReporteRemoteDataSource(
    private val api: ReporteApi
) : ReporteRemoteDataSource {

    override suspend fun enviar(reporte: Reporte): Result<String> =
        try {
            val creado = api.crear(reporte.aDto())
            creado.id?.let { Result.success(it) }
                ?: Result.failure(IllegalStateException("El servidor no devolvió un id"))
        } catch (e: IOException) {          // sin conexión o timeout
            Result.failure(e)
        } catch (e: HttpException) {        // respuesta 4xx / 5xx
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
}
