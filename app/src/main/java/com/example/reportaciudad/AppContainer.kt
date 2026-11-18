package com.example.reportaciudad

import android.content.Context
import com.example.reportaciudad.data.local.AlmacenFotos
import com.example.reportaciudad.data.local.SesionStore
import com.example.reportaciudad.data.local.UbicacionActual
import com.example.reportaciudad.data.local.db.ReportaCiudadDatabase
import com.example.reportaciudad.data.remote.ClienteApi
import com.example.reportaciudad.data.remote.CloudinaryApi
import com.example.reportaciudad.data.remote.FotoRemoteDataSource
import com.example.reportaciudad.data.remote.ReporteApi
import com.example.reportaciudad.data.remote.ReporteRemoteDataSource
import com.example.reportaciudad.data.remote.UsuarioApi
import com.example.reportaciudad.data.remote.UsuarioRemoteDataSource
import com.example.reportaciudad.repository.AdminRepository
import com.example.reportaciudad.repository.AuthRepository
import com.example.reportaciudad.repository.ReporteRepository


class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val baseDeDatos by lazy { ReportaCiudadDatabase.crear(appContext) }

    private val retrofit by lazy { ClienteApi.crear() }

    private val retrofitCloudinary by lazy { ClienteApi.crear(BuildConfig.CLOUDINARY_URL) }

    // La comparten el vecino y el administrador
    private val reporteRemoto by lazy { ReporteRemoteDataSource(retrofit.create(ReporteApi::class.java)) }

    private val fotoRemota by lazy {
        FotoRemoteDataSource(
            api = retrofitCloudinary.create(CloudinaryApi::class.java),
            preset = BuildConfig.CLOUDINARY_PRESET
        )
    }

    private val almacenFotos by lazy { AlmacenFotos(appContext) }

    val reporteRepository: ReporteRepository by lazy {
        ReporteRepository(
            dao = baseDeDatos.reporteDao(),
            remoto = reporteRemoto,
            fotos = fotoRemota,
            almacenFotos = almacenFotos,
            ubicacionActual = UbicacionActual(appContext)
        )
    }

    val adminRepository: AdminRepository by lazy {
        AdminRepository(remoto = reporteRemoto, fotos = fotoRemota, almacenFotos = almacenFotos)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(
            remoto = UsuarioRemoteDataSource(retrofit.create(UsuarioApi::class.java)),
            sesionStore = SesionStore(appContext)
        )
    }
}
