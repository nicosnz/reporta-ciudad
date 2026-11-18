package com.example.reportaciudad.data.remote

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CloudinaryFotoRemoteDataSource(
    private val api: CloudinaryApi,
    // Preset "unsigned": la configuración (carpeta, formatos, tamaño) vive en Cloudinary
    private val preset: String
) : FotoRemoteDataSource {

    override suspend fun subir(archivo: File): Result<String> =
        llamarApi {
            val parte = MultipartBody.Part.createFormData(
                "file",
                archivo.name,
                archivo.asRequestBody("image/jpeg".toMediaType())
            )
            api.subir(parte, preset.toRequestBody(MultipartBody.FORM)).url
        }
}
