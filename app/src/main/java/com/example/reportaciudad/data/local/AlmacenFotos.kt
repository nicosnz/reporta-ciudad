package com.example.reportaciudad.data.local

import android.content.Context
import java.io.File

// Fotos de los reportes en la carpeta privada de la app (files/fotos/)
class AlmacenFotos(context: Context) {

    private val carpeta = File(context.filesDir, CARPETA)

    fun crearArchivo(): File {
        carpeta.mkdirs()
        return File(carpeta, "reporte_${System.currentTimeMillis()}.jpg")
    }

    fun borrar(ruta: String?) {
        ruta?.let { File(it).delete() }
    }

    private companion object {
        const val CARPETA = "fotos"
    }
}
