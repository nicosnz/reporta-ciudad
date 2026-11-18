package com.example.reportaciudad.repository

import com.example.reportaciudad.data.local.SesionStore
import com.example.reportaciudad.data.model.Rol
import com.example.reportaciudad.data.model.Sesion
import com.example.reportaciudad.data.remote.UsuarioRemoteDataSource
import kotlinx.coroutines.flow.Flow
import java.io.IOException

sealed interface ResultadoLogin {
    data class Exito(val sesion: Sesion) : ResultadoLogin
    data object CredencialesIncorrectas : ResultadoLogin
    data object SinConexion : ResultadoLogin
    data object ErrorServidor : ResultadoLogin
}

// Inicio y cierre de sesión. La sesión se guarda en el teléfono.
class AuthRepository(
    private val remoto: UsuarioRemoteDataSource,
    private val sesionStore: SesionStore
) {

    // null si nadie inició sesión
    val sesion: Flow<Sesion?> = sesionStore.sesion

    suspend fun iniciarSesion(usuario: String, contrasena: String): ResultadoLogin {
        val nombreUsuario = usuario.trim()
        val encontrados = remoto.buscar(nombreUsuario).getOrElse { error ->
            return if (error is IOException) ResultadoLogin.SinConexion else ResultadoLogin.ErrorServidor
        }

        // El filtro de MockAPI es parcial: se exige el usuario exacto
        val dto = encontrados.find { it.usuario == nombreUsuario && it.contrasena == contrasena }
            ?: return ResultadoLogin.CredencialesIncorrectas

        val rol = Rol.entries.find { it.name == dto.rol.uppercase() }
            ?: return ResultadoLogin.ErrorServidor

        val nueva = Sesion(
            usuarioId = dto.id,
            usuario = dto.usuario,
            nombre = dto.nombre?.takeIf { it.isNotBlank() } ?: dto.usuario,
            rol = rol
        )
        sesionStore.guardar(nueva)
        return ResultadoLogin.Exito(nueva)
    }

    suspend fun cerrarSesion() = sesionStore.borrar()
}
