package com.example.reportaciudad.repository

import com.example.reportaciudad.data.model.Sesion
import kotlinx.coroutines.flow.Flow

sealed interface ResultadoLogin {
    data class Exito(val sesion: Sesion) : ResultadoLogin
    data object CredencialesIncorrectas : ResultadoLogin
    data object SinConexion : ResultadoLogin
    data object ErrorServidor : ResultadoLogin
}

// Inicio y cierre de sesión. La sesión se guarda en el teléfono.
interface AuthRepository {

    // null si nadie inició sesión
    val sesion: Flow<Sesion?>

    suspend fun iniciarSesion(usuario: String, contrasena: String): ResultadoLogin

    suspend fun cerrarSesion()
}
