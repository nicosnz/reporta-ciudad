package com.example.reportaciudad

import android.content.Context
import com.example.reportaciudad.data.local.AlmacenFotos
import com.example.reportaciudad.data.local.SesionStore
import com.example.reportaciudad.data.local.UbicacionActual
import com.example.reportaciudad.data.local.db.ReportaCiudadDatabase
import com.example.reportaciudad.data.remote.ClienteApi
import com.example.reportaciudad.data.remote.CloudinaryApi
import com.example.reportaciudad.data.remote.CloudinaryFotoRemoteDataSource
import com.example.reportaciudad.data.remote.ReporteApi
import com.example.reportaciudad.data.remote.RetrofitReporteRemoteDataSource
import com.example.reportaciudad.data.remote.RetrofitUsuarioRemoteDataSource
import com.example.reportaciudad.data.remote.UsuarioApi
import com.example.reportaciudad.repository.AdminRepository
import com.example.reportaciudad.repository.AdminRepositoryImpl
import com.example.reportaciudad.repository.AuthRepository
import com.example.reportaciudad.repository.AuthRepositoryImpl
import com.example.reportaciudad.repository.ReporteRepository
import com.example.reportaciudad.repository.ReporteRepositoryImpl


class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val baseDeDatos by lazy { ReportaCiudadDatabase.crear(appContext) }

    private val retrofit by lazy { ClienteApi.crear() }

    private val retrofitCloudinary by lazy { ClienteApi.crear(BuildConfig.CLOUDINARY_URL) }

    // La comparten el vecino y el administrador
    private val reporteRemoto by lazy { RetrofitReporteRemoteDataSource(retrofit.create(ReporteApi::class.java)) }

    val reporteRepository: ReporteRepository by lazy {
        ReporteRepositoryImpl(
            dao = baseDeDatos.reporteDao(),
            remoto = reporteRemoto,
            fotos = CloudinaryFotoRemoteDataSource(
                api = retrofitCloudinary.create(CloudinaryApi::class.java),
                preset = BuildConfig.CLOUDINARY_PRESET
            ),
            almacenFotos = AlmacenFotos(appContext),
            ubicacionActual = UbicacionActual(appContext)
        )
    }

    val adminRepository: AdminRepository by lazy { AdminRepositoryImpl(remoto = reporteRemoto) }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            remoto = RetrofitUsuarioRemoteDataSource(retrofit.create(UsuarioApi::class.java)),
            sesionStore = SesionStore(appContext)
        )
    }
}
