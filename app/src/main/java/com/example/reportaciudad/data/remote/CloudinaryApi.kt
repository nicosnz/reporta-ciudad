package com.example.reportaciudad.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CloudinaryApi {

    @Multipart
    @POST("image/upload")
    suspend fun subir(
        @Part archivo: MultipartBody.Part,
        @Part("upload_preset") preset: RequestBody
    ): FotoSubidaDto
}

@Serializable
data class FotoSubidaDto(
    @SerialName("secure_url") val url: String
)
