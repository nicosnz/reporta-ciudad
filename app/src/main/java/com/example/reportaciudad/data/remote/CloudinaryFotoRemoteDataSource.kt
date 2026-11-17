package com.example.reportaciudad.data.remote

import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class CloudinaryFotoRemoteDataSource(
    private val api: CloudinaryApi,
    // Preset "unsigned": la configuración (carpeta, formatos, tamaño) vive en Cloudinary
    private val preset: String
) : FotoRemoteDataSource {

    override suspend fun subir(archivo: File): Result<String> =
        try {
            val parte = MultipartBody.Part.createFormData(
                "file",
                archivo.name,
                archivo.asRequestBody("image/jpeg".toMediaType())
            )
            val respuesta = api.subir(parte, preset.toRequestBody(MultipartBody.FORM))
            Result.success(respuesta.url)
        } catch (e: IOException) {          // sin conexión, timeout o el archivo no se pudo leer
            Result.failure(e)
        } catch (e: HttpException) {        // preset inválido, archivo rechazado, cuota…
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
}
