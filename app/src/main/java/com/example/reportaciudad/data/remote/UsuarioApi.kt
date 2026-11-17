package com.example.reportaciudad.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// Endpoints de MockAPI: <API_BASE_URL>/usuarios
interface UsuarioApi {

    // MockAPI filtra por coincidencia parcial ("vecino1" también trae "vecino10"):
    // la comparación exacta la hace el repositorio
    @GET("usuarios")
    suspend fun buscar(@Query("usuario") usuario: String): List<UsuarioDto>
}
