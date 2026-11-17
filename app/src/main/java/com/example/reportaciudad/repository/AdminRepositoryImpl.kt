package com.example.reportaciudad.repository

import com.example.reportaciudad.data.model.ReporteServidor
import com.example.reportaciudad.data.remote.ReporteRemoteDataSource

class AdminRepositoryImpl(
    private val remoto: ReporteRemoteDataSource
) : AdminRepository {

    override suspend fun reportes(): Result<List<ReporteServidor>> =
        remoto.listarTodos().map { lista -> lista.sortedByDescending { it.fechaHora } }
}
