package com.example.reportaciudad.data.remote

import retrofit2.HttpException

// Usuarios en el servidor (MockAPI). Para cambiar de login solo hay que tocar esta clase y UsuarioApi.
class UsuarioRemoteDataSource(
    private val api: UsuarioApi
) {

    // Usuarios cuyo nombre de usuario contiene el texto buscado.
    // Falla con IOException si no hay conexión.
    suspend fun buscar(usuario: String): Result<List<UsuarioDto>> =
        llamarApi { api.buscar(usuario) }.recoverCatching { error ->
            // MockAPI responde 404 cuando el filtro no encuentra a nadie
            if (error is HttpException && error.code() == 404) emptyList() else throw error
        }
}
