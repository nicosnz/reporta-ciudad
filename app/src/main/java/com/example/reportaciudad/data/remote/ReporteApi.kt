package com.example.reportaciudad.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Endpoints de MockAPI: <API_BASE_URL>/reportes
interface ReporteApi {

    @POST("reportes")
    suspend fun crear(@Body reporte: ReporteDto): ReporteDto

    // Todos los reportes: los usa el administrador
    @GET("reportes")
    suspend fun listar(): List<ReporteDto>

    @GET("reportes/{id}")
    suspend fun obtener(@Path("id") id: String): ReporteDto
}
