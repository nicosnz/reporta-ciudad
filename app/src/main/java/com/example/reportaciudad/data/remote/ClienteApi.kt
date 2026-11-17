package com.example.reportaciudad.data.remote

import com.example.reportaciudad.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object ClienteApi {

    // MockAPI agrega campos propios (createdAt…): se ignoran en vez de fallar
    private val json = Json { ignoreUnknownKeys = true }

    // Un solo Retrofit para toda la app: ReporteApi y UsuarioApi comparten conexión
    fun crear(baseUrl: String = BuildConfig.API_BASE_URL): Retrofit {
        val http = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .apply {
                // El log de peticiones solo en debug: incluye coordenadas y contraseñas
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(http)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
