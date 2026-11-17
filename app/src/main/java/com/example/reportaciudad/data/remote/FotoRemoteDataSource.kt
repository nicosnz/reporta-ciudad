package com.example.reportaciudad.data.remote

import java.io.File

// Dónde se guardan las fotos de los reportes. Hoy Cloudinary; se puede cambiar sin tocar el repositorio.
interface FotoRemoteDataSource {

    // Devuelve la URL pública de la foto. Falla con IOException si no hay conexión.
    suspend fun subir(archivo: File): Result<String>
}
