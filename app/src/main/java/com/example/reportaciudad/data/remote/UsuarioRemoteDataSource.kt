package com.example.reportaciudad.data.remote

import retrofit2.HttpException

class UsuarioRemoteDataSource(
    private val api: UsuarioApi
) {

    suspend fun buscar(usuario: String): Result<List<UsuarioDto>> =
        llamarApi { api.buscar(usuario) }.recoverCatching { error ->
            if (error is HttpException && error.code() == 404) emptyList() else throw error
        }
}
