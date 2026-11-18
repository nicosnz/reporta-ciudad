package com.example.reportaciudad.repository

import android.net.Uri
import com.example.reportaciudad.data.local.AlmacenFotos
import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.MotivoRechazo
import com.example.reportaciudad.data.model.ReporteServidor
import com.example.reportaciudad.data.remote.FotoRemoteDataSource
import com.example.reportaciudad.data.remote.ReporteRemoteDataSource
import java.io.File

class AdminRepository(
    private val remoto: ReporteRemoteDataSource,
    private val fotos: FotoRemoteDataSource,
    private val almacenFotos: AlmacenFotos
) {

    suspend fun reportes(): Result<List<ReporteServidor>> =
        remoto.listarTodos().map { lista -> lista.sortedByDescending { it.fechaHora } }

    suspend fun reporte(remoteId: String): Result<ReporteServidor> =
        remoto.obtener(remoteId)

    suspend fun avanzar(remoteId: String): Result<ReporteServidor> =
        cambiar(remoteId) { actual ->
            actual.estado.siguiente
                ?.takeIf { it != EstadoReporte.RESUELTO }
                ?.let { actual.copy(estado = it) }
        }

    suspend fun rechazar(remoteId: String, motivo: MotivoRechazo): Result<ReporteServidor> =
        cambiar(remoteId) { actual ->
            if (actual.estado.esFinal) null
            else actual.copy(estado = EstadoReporte.RECHAZADO, motivoRechazo = motivo)
        }

    suspend fun resolver(remoteId: String, foto: File): Result<ReporteServidor> {
        val actual = remoto.obtener(remoteId).getOrElse { return Result.failure(it) }
        if (actual.estado != EstadoReporte.ASIGNADO) return Result.failure(cambioNoPermitido(actual))
        val url = fotos.subir(foto).getOrElse { return Result.failure(it) }
        return remoto.actualizar(actual.copy(estado = EstadoReporte.RESUELTO, fotoArregloUrl = url))
    }

    fun nuevoArchivoFoto(): File = almacenFotos.crearArchivo()

    suspend fun importarFoto(uri: Uri): File? = almacenFotos.importar(uri)

    fun descartarFoto(ruta: String?) = almacenFotos.borrar(ruta)

    private suspend fun cambiar(
        remoteId: String,
        transformar: (ReporteServidor) -> ReporteServidor?
    ): Result<ReporteServidor> {
        val actual = remoto.obtener(remoteId).getOrElse { return Result.failure(it) }
        val nuevo = transformar(actual) ?: return Result.failure(cambioNoPermitido(actual))
        return remoto.actualizar(nuevo)
    }

    private fun cambioNoPermitido(reporte: ReporteServidor) =
        IllegalStateException("Cambio no permitido desde ${reporte.estado.name}")
}
