package com.example.reportaciudad.data.model

enum class Rol {
    VECINO,
    ADMIN
}

data class Sesion(
    val usuarioId: String,
    val usuario: String,
    val nombre: String,
    val rol: Rol
)
