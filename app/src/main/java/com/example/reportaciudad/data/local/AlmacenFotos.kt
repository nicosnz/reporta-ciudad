package com.example.reportaciudad.data.local

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class AlmacenFotos(private val context: Context) {

    private val carpeta = File(context.filesDir, CARPETA)

    fun crearArchivo(): File {
        carpeta.mkdirs()
        return File(carpeta, "reporte_${System.currentTimeMillis()}.jpg")
    }

    fun borrar(ruta: String?) {
        ruta?.let { File(it).delete() }
    }

    suspend fun importar(uri: Uri): File? = withContext(Dispatchers.IO) {
        val archivo = crearArchivo()
        try {
            val entrada = context.contentResolver.openInputStream(uri)
            if (entrada == null) {
                null
            } else {
                entrada.use { origen -> archivo.outputStream().use { destino -> origen.copyTo(destino) } }
                archivo
            }
        } catch (e: IOException) {
            archivo.delete()
            null
        } catch (e: SecurityException) {
            archivo.delete()
            null
        }
    }

    private companion object {
        const val CARPETA = "fotos"
    }
}
