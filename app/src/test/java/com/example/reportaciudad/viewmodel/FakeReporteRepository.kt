package com.example.reportaciudad.viewmodel

import com.example.reportaciudad.data.model.EstadoEnvio
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.data.model.ReporteFormulario
import com.example.reportaciudad.data.model.Ubicacion
import com.example.reportaciudad.repository.ReporteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.io.File

// Repositorio en memoria para probar los ViewModels sin Room, red ni GPS
class FakeReporteRepository : ReporteRepository {

    val lista = MutableStateFlow<List<Reporte>>(emptyList())
    val fotosBorradas = mutableListOf<String>()
    val enviados = mutableListOf<ReporteFormulario>()
    var ubicacionDelGps: Ubicacion? = Ubicacion(-17.7836, -63.1812)
    var resultadoEnvio = EstadoEnvio.ENVIADO
    var reintentos = 0

    override val reportes: Flow<List<Reporte>> = lista

    override fun reporte(id: Long): Flow<Reporte?> = lista.map { it.firstOrNull { r -> r.id == id } }

    override suspend fun enviar(formulario: ReporteFormulario): EstadoEnvio {
        enviados += formulario
        return resultadoEnvio
    }

    override suspend fun reintentarPendientes() {
        reintentos++
    }

    override fun nuevoArchivoFoto(): File = File("foto_${System.nanoTime()}.jpg")

    override fun descartarFoto(ruta: String?) {
        ruta?.let { fotosBorradas += it }
    }

    override suspend fun obtenerUbicacion(): Ubicacion? = ubicacionDelGps
}
