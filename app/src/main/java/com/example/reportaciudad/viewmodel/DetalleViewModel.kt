package com.example.reportaciudad.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.repository.ReporteRepository
import com.example.reportaciudad.repository.ResultadoCancelacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data class Encontrado(val reporte: Reporte) : DetalleUiState
    data object NoExiste : DetalleUiState
}

enum class ErrorCancelacion {
    SIN_CONEXION,
    YA_EN_REVISION
}

data class CancelacionUiState(
    val dialogoAbierto: Boolean = false,
    val cancelando: Boolean = false,
    val error: ErrorCancelacion? = null,
    val eliminado: Boolean = false
)

class DetalleViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ReporteRepository
) : ViewModel() {

    private val id: Long = checkNotNull(savedStateHandle.get<Long>(ARG_ID)) { "Falta el id del reporte" }

    val estado: StateFlow<DetalleUiState> = repository.reporte(id)
        .map { reporte -> reporte?.let { DetalleUiState.Encontrado(it) } ?: DetalleUiState.NoExiste }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalleUiState.Cargando)

    private val _cancelacion = MutableStateFlow(CancelacionUiState())
    val cancelacion: StateFlow<CancelacionUiState> = _cancelacion.asStateFlow()

    init {
        viewModelScope.launch { repository.actualizarEstado(id) }
    }

    fun abrirCancelacion() {
        _cancelacion.update { it.copy(dialogoAbierto = true, error = null) }
    }

    fun cerrarCancelacion() {
        if (_cancelacion.value.cancelando) return
        _cancelacion.update { it.copy(dialogoAbierto = false) }
    }

    fun confirmarCancelacion() {
        if (_cancelacion.value.cancelando) return
        _cancelacion.update { it.copy(cancelando = true, error = null) }
        viewModelScope.launch {
            val resultado = repository.cancelar(id)
            _cancelacion.update {
                when (resultado) {
                    ResultadoCancelacion.ELIMINADO -> CancelacionUiState(eliminado = true)
                    ResultadoCancelacion.CANCELADO -> CancelacionUiState()
                    ResultadoCancelacion.NO_PERMITIDO ->
                        CancelacionUiState(error = ErrorCancelacion.YA_EN_REVISION)
                    ResultadoCancelacion.SIN_CONEXION ->
                        CancelacionUiState(error = ErrorCancelacion.SIN_CONEXION)
                }
            }
        }
    }

    companion object {
        const val ARG_ID = "id"
    }
}
