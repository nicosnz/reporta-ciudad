package com.example.reportaciudad.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Subida de imágenes a Cloudinary: <CLOUDINARY_URL>/image/upload
interface CloudinaryApi {

    @Multipart
    @POST("image/upload")
    suspend fun subir(
        @Part archivo: MultipartBody.Part,
        @Part("upload_preset") preset: RequestBody
    ): FotoSubidaDto
}

// Cloudinary devuelve muchos datos; solo interesa la URL https de la imagen
@Serializable
data class FotoSubidaDto(
    @SerialName("secure_url") val url: String
)
