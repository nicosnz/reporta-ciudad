package com.example.reportaciudad.data.remote

import kotlinx.serialization.Serializable

// JSON del recurso "usuarios" en MockAPI.
// Solo para la demo: MockAPI es público y la contraseña viaja y se guarda en texto plano.
@Serializable
data class UsuarioDto(
    val id: String,
    val usuario: String,
    val contrasena: String,
    // "VECINO" o "ADMIN"
    val rol: String,
    val nombre: String? = null
)
