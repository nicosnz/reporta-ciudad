package com.example.reportaciudad.repository

import com.example.reportaciudad.data.model.EstadoEnvio
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.data.model.ReporteFormulario
import com.example.reportaciudad.data.model.Ubicacion
import kotlinx.coroutines.flow.Flow
import java.io.File

// Único punto de acceso a los datos para los ViewModels.
// Room es la fuente de verdad: la UI siempre lee de la base local, el servidor solo recibe.
interface ReporteRepository {

    val reportes: Flow<List<Reporte>>

    fun reporte(id: Long): Flow<Reporte?>

    // Guarda el reporte en el teléfono y lo intenta enviar al servidor.
    // Devuelve cómo quedó: ENVIADO, o ERROR si no hubo conexión (se reintenta después).
    suspend fun enviar(formulario: ReporteFormulario): EstadoEnvio

    // Reenvía los reportes que quedaron sin enviar
    suspend fun reintentarPendientes()

    fun nuevoArchivoFoto(): File

    fun descartarFoto(ruta: String?)

    suspend fun obtenerUbicacion(): Ubicacion?
}
