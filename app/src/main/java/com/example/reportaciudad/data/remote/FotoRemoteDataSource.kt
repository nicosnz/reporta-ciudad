package com.example.reportaciudad.data.remote

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class FotoRemoteDataSource(
    private val api: CloudinaryApi,
    private val preset: String
) {

    suspend fun subir(archivo: File): Result<String> =
        llamarApi {
            val parte = MultipartBody.Part.createFormData(
                "file",
                archivo.name,
                archivo.asRequestBody("image/jpeg".toMediaType())
            )
            api.subir(parte, preset.toRequestBody(MultipartBody.FORM)).url
        }
}
