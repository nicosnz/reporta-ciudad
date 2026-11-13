package com.example.reportaciudad.data.remote

import com.example.reportaciudad.data.model.Reporte
import kotlinx.coroutines.delay

// Simula el servidor sin red: para tests y para probar la app sin conexión.
class FakeReporteRemoteDataSource(
    private val falla: Boolean = false,
    private val demoraMs: Long = 500
) : ReporteRemoteDataSource {

    private var siguienteId = 1

    override suspend fun enviar(reporte: Reporte): Result<String> {
        delay(demoraMs)
        return if (falla) Result.failure(java.io.IOException("Sin conexión (simulado)"))
        else Result.success((siguienteId++).toString())
    }
}
