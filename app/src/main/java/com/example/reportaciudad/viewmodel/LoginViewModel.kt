package com.example.reportaciudad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportaciudad.data.model.Sesion
import com.example.reportaciudad.repository.AuthRepository
import com.example.reportaciudad.repository.ResultadoLogin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ErrorLogin {
    CREDENCIALES,
    SIN_CONEXION,
    SERVIDOR
}

data class LoginUiState(
    val usuario: String = "",
    val contrasena: String = "",
    val mostrarContrasena: Boolean = false,
    val ingresando: Boolean = false,
    val error: ErrorLogin? = null,
    val sesion: Sesion? = null
) {
    val puedeIngresar: Boolean
        get() = usuario.isNotBlank() && contrasena.isNotEmpty() && !ingresando && sesion == null
}

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun cambiarUsuario(texto: String) = _estado.update { it.copy(usuario = texto, error = null) }

    fun cambiarContrasena(texto: String) = _estado.update { it.copy(contrasena = texto, error = null) }

    fun alternarMostrarContrasena() = _estado.update { it.copy(mostrarContrasena = !it.mostrarContrasena) }

    fun ingresar() {
        val actual = _estado.value
        if (!actual.puedeIngresar) return
        _estado.update { it.copy(ingresando = true, error = null) }
        viewModelScope.launch {
            val resultado = repository.iniciarSesion(actual.usuario, actual.contrasena)
            _estado.update {
                when (resultado) {
                    is ResultadoLogin.Exito -> it.copy(ingresando = false, sesion = resultado.sesion)
                    ResultadoLogin.CredencialesIncorrectas -> it.copy(ingresando = false, error = ErrorLogin.CREDENCIALES)
                    ResultadoLogin.SinConexion -> it.copy(ingresando = false, error = ErrorLogin.SIN_CONEXION)
                    ResultadoLogin.ErrorServidor -> it.copy(ingresando = false, error = ErrorLogin.SERVIDOR)
                }
            }
        }
    }
}
