package com.example.reportaciudad.data.remote

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

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
