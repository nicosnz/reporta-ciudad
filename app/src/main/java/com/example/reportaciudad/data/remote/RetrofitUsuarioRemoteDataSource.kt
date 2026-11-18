package com.example.reportaciudad.data.remote

import retrofit2.HttpException

class RetrofitUsuarioRemoteDataSource(
    private val api: UsuarioApi
) : UsuarioRemoteDataSource {

    override suspend fun buscar(usuario: String): Result<List<UsuarioDto>> =
        llamarApi { api.buscar(usuario) }.recoverCatching { error ->
            // MockAPI responde 404 cuando el filtro no encuentra a nadie
            if (error is HttpException && error.code() == 404) emptyList() else throw error
        }
}
