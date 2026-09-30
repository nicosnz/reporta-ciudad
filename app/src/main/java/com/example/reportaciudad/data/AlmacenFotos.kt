package com.example.reportaciudad.data

import android.content.Context
import java.io.File

// Fotos de los reportes, guardadas en la carpeta privada de la app (files/fotos).
// No aparecen en la galería y se borran al desinstalar la app.
object AlmacenFotos {

    private const val CARPETA = "fotos"

    // Archivo vacío donde la cámara va a escribir la próxima foto; el nombre usa la hora para no repetirse
    fun crearArchivo(context: Context): File {
        val carpeta = File(context.filesDir, CARPETA).apply { mkdirs() }
        return File(carpeta, "reporte_${System.currentTimeMillis()}.jpg")
    }

    fun borrar(ruta: String?) {
        ruta?.let { File(it).delete() }
    }
}
