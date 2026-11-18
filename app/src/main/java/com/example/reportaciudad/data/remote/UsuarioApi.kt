package com.example.reportaciudad.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface UsuarioApi {

    @GET("usuarios")
    suspend fun buscar(@Query("usuario") usuario: String): List<UsuarioDto>
}
