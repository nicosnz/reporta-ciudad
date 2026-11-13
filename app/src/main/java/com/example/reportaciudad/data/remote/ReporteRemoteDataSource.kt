package com.example.reportaciudad.data.remote

import com.example.reportaciudad.data.model.Reporte

// Fuente remota de reportes. El repositorio solo conoce esta interfaz:
// se puede cambiar MockAPI por un servidor real (o por el fake en tests) sin tocar nada más.
interface ReporteRemoteDataSource {

    // Devuelve el id que le asignó el servidor
    suspend fun enviar(reporte: Reporte): Result<String>
}
