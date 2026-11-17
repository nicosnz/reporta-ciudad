package com.example.reportaciudad.data.remote

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class RetrofitUsuarioRemoteDataSource(
    private val api: UsuarioApi
) : UsuarioRemoteDataSource {

    override suspend fun buscar(usuario: String): Result<List<UsuarioDto>> =
        try {
            Result.success(api.buscar(usuario))
        } catch (e: IOException) {          // sin conexión o timeout
            Result.failure(e)
        } catch (e: HttpException) {
            // MockAPI responde 404 cuando el filtro no encuentra a nadie
            if (e.code() == 404) Result.success(emptyList()) else Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
}
