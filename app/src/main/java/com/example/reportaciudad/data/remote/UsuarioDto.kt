package com.example.reportaciudad.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class UsuarioDto(
    val id: String,
    val usuario: String,
    val contrasena: String,
    val rol: String,
    val nombre: String? = null
)
