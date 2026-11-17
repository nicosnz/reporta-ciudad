package com.example.reportaciudad.data.remote

// Fuente remota de usuarios. El repositorio solo conoce esta interfaz:
// se puede cambiar MockAPI por un login real sin tocar nada más.
interface UsuarioRemoteDataSource {

    // Usuarios cuyo nombre de usuario contiene el texto buscado.
    // Falla con IOException si no hay conexión.
    suspend fun buscar(usuario: String): Result<List<UsuarioDto>>
}
