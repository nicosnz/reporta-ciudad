package com.example.reportaciudad.data

import android.content.Context
import java.io.File


object AlmacenFotos {

    private const val CARPETA = "fotos"


    fun crearArchivo(context: Context): File {
        val carpeta = File(context.filesDir, CARPETA).apply { mkdirs() }
        return File(carpeta, "reporte_${System.currentTimeMillis()}.jpg")
    }

    fun borrar(ruta: String?) {
        ruta?.let { File(it).delete() }
    }
}
