package com.example.reportaciudad

import android.content.Context
import com.example.reportaciudad.data.local.AlmacenFotos
import com.example.reportaciudad.data.local.UbicacionActual
import com.example.reportaciudad.data.local.db.ReportaCiudadDatabase
import com.example.reportaciudad.data.remote.ClienteApi
import com.example.reportaciudad.data.remote.RetrofitReporteRemoteDataSource
import com.example.reportaciudad.repository.ReporteRepository
import com.example.reportaciudad.repository.ReporteRepositoryImpl

// Inyección de dependencias manual: acá se arma cada pieza una sola vez.
// Para probar sin red, cambiar RetrofitReporteRemoteDataSource por FakeReporteRemoteDataSource().
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val baseDeDatos by lazy { ReportaCiudadDatabase.crear(appContext) }

    val reporteRepository: ReporteRepository by lazy {
        ReporteRepositoryImpl(
            dao = baseDeDatos.reporteDao(),
            remoto = RetrofitReporteRemoteDataSource(ClienteApi.crear()),
            almacenFotos = AlmacenFotos(appContext),
            ubicacionActual = UbicacionActual(appContext)
        )
    }
}
