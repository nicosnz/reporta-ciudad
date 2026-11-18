package com.example.reportaciudad.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReporteApi {

    @POST("reportes")
    suspend fun crear(@Body reporte: ReporteDto): ReporteDto

    @GET("reportes")
    suspend fun listar(): List<ReporteDto>

    @GET("reportes/{id}")
    suspend fun obtener(@Path("id") id: String): ReporteDto

    @PUT("reportes/{id}")
    suspend fun actualizar(@Path("id") id: String, @Body reporte: ReporteDto): ReporteDto
}
