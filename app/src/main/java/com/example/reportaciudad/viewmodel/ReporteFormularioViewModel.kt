package com.example.reportaciudad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportaciudad.data.model.EstadoEnvio
import com.example.reportaciudad.data.model.ReporteFormulario
import com.example.reportaciudad.data.model.TipoEmergencia
import com.example.reportaciudad.data.model.TipoProblema
import com.example.reportaciudad.data.model.Ubicacion
import com.example.reportaciudad.repository.ReporteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

enum class Envio {
    INACTIVO,
    ENVIANDO,
    ENVIADO,
    // Quedó guardado en el teléfono; se reenvía cuando haya conexión
    GUARDADO_SIN_CONEXION
}

data class FormularioUiState(
    val borrador: ReporteFormulario = ReporteFormulario(),
    // Ubicación que encontró el GPS en el paso 4, antes de que el usuario la confirme
    val ubicacion: Ubicacion? = null,
    val buscandoUbicacion: Boolean = false,
    val sinUbicacion: Boolean = false,
    val envio: Envio = Envio.INACTIVO
) {
    val terminado: Boolean get() = envio == Envio.ENVIADO || envio == Envio.GUARDADO_SIN_CONEXION
}

// Borrador del flujo "Reportar" (pasos 1 a 5). Vive mientras el usuario está dentro del flujo:
// todas las pantallas del flujo comparten esta misma instancia.
class ReporteFormularioViewModel(private val repository: ReporteRepository) : ViewModel() {

    private val _estado = MutableStateFlow(FormularioUiState())
    val estado: StateFlow<FormularioUiState> = _estado.asStateFlow()

    private val borrador get() = _estado.value.borrador

    // Paso 2
    fun elegirEmergencia(tipo: TipoEmergencia) = actualizarBorrador {
        it.copy(emergencia = tipo, problema = null)
    }

    fun elegirProblema(tipo: TipoProblema) = actualizarBorrador {
        it.copy(problema = tipo, emergencia = null)
    }

    // Paso 3
    fun nuevoArchivoFoto(): File = repository.nuevoArchivoFoto()

    fun fotoSacada(ruta: String) {
        if (borrador.fotoRuta != ruta) repository.descartarFoto(borrador.fotoRuta)
        actualizarBorrador { it.copy(fotoRuta = ruta) }
    }

    // "Repetir" o volver atrás desde el paso 3: la foto ya no sirve
    fun descartarFoto() {
        repository.descartarFoto(borrador.fotoRuta)
        actualizarBorrador { it.copy(fotoRuta = null) }
    }

    // Paso 4
    fun buscarUbicacion() {
        if (_estado.value.buscandoUbicacion) return
        _estado.update { it.copy(buscandoUbicacion = true, sinUbicacion = false) }
        viewModelScope.launch {
            val encontrada = repository.obtenerUbicacion()
            _estado.update {
                it.copy(
                    ubicacion = encontrada ?: it.ubicacion,
                    buscandoUbicacion = false,
                    sinUbicacion = encontrada == null
                )
            }
        }
    }

    fun confirmarUbicacion() {
        val ubicacion = _estado.value.ubicacion ?: return
        actualizarBorrador { it.copy(latitud = ubicacion.latitud, longitud = ubicacion.longitud) }
    }

    // Paso 5
    fun enviar() {
        if (_estado.value.envio != Envio.INACTIVO || !borrador.estaCompleto) return
        _estado.update { it.copy(envio = Envio.ENVIANDO) }
        viewModelScope.launch {
            val resultado = repository.enviar(borrador)
            _estado.update {
                it.copy(envio = if (resultado == EstadoEnvio.ENVIADO) Envio.ENVIADO else Envio.GUARDADO_SIN_CONEXION)
            }
        }
    }

    // El usuario salió del flujo: si no envió, la foto queda huérfana y se borra
    override fun onCleared() {
        if (!_estado.value.terminado && _estado.value.envio != Envio.ENVIANDO) {
            repository.descartarFoto(borrador.fotoRuta)
        }
    }

    private fun actualizarBorrador(cambio: (ReporteFormulario) -> ReporteFormulario) =
        _estado.update { it.copy(borrador = cambio(it.borrador)) }
}
