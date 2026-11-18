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
    GUARDADO_SIN_CONEXION
}

data class FormularioUiState(
    val borrador: ReporteFormulario = ReporteFormulario(),
    val ubicacion: Ubicacion? = null,
    val buscandoUbicacion: Boolean = false,
    val sinUbicacion: Boolean = false,
    val envio: Envio = Envio.INACTIVO
) {
    val terminado: Boolean get() = envio == Envio.ENVIADO || envio == Envio.GUARDADO_SIN_CONEXION
}

class ReporteFormularioViewModel(private val repository: ReporteRepository) : ViewModel() {

    private val _estado = MutableStateFlow(FormularioUiState())
    val estado: StateFlow<FormularioUiState> = _estado.asStateFlow()

    private val borrador get() = _estado.value.borrador

    fun elegirEmergencia(tipo: TipoEmergencia) = actualizarBorrador {
        it.copy(emergencia = tipo, problema = null)
    }

    fun elegirProblema(tipo: TipoProblema) = actualizarBorrador {
        it.copy(problema = tipo, emergencia = null)
    }

    fun nuevoArchivoFoto(): File = repository.nuevoArchivoFoto()

    fun fotoSacada(ruta: String) {
        if (borrador.fotoRuta != ruta) repository.descartarFoto(borrador.fotoRuta)
        actualizarBorrador { it.copy(fotoRuta = ruta) }
    }

    fun descartarFoto() {
        repository.descartarFoto(borrador.fotoRuta)
        actualizarBorrador { it.copy(fotoRuta = null) }
    }

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

    override fun onCleared() {
        if (!_estado.value.terminado && _estado.value.envio != Envio.ENVIANDO) {
            repository.descartarFoto(borrador.fotoRuta)
        }
    }

    private fun actualizarBorrador(cambio: (ReporteFormulario) -> ReporteFormulario) =
        _estado.update { it.copy(borrador = cambio(it.borrador)) }
}
