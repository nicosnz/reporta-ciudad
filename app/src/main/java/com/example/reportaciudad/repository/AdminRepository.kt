package com.example.reportaciudad.repository

import android.net.Uri
import com.example.reportaciudad.data.model.MotivoRechazo
import com.example.reportaciudad.data.model.ReporteServidor
import java.io.File

interface AdminRepository {

    suspend fun reportes(): Result<List<ReporteServidor>>

    suspend fun reporte(remoteId: String): Result<ReporteServidor>

    suspend fun avanzar(remoteId: String): Result<ReporteServidor>

    suspend fun rechazar(remoteId: String, motivo: MotivoRechazo): Result<ReporteServidor>

    suspend fun resolver(remoteId: String, foto: File): Result<ReporteServidor>

    fun nuevoArchivoFoto(): File

    suspend fun importarFoto(uri: Uri): File?

    fun descartarFoto(ruta: String?)
}
