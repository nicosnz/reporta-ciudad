package com.example.reportaciudad.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Endpoints de MockAPI: <API_BASE_URL>/reportes
interface ReporteApi {

    @GET("reportes")
    suspend fun listar(): List<ReporteDto>

    @POST("reportes")
    suspend fun crear(@Body reporte: ReporteDto): ReporteDto
}
