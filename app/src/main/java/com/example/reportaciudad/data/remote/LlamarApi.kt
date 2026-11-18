package com.example.reportaciudad.data.remote

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

// Ejecuta una llamada al servidor y convierte los errores esperables en Result.failure:
// sin conexión o timeout (IOException), respuesta 4xx/5xx (HttpException) o JSON inesperado.
internal inline fun <T> llamarApi(bloque: () -> T): Result<T> =
    try {
        Result.success(bloque())
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: SerializationException) {
        Result.failure(e)
    }
