package com.example.reportaciudad.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportaciudad.data.model.MotivoRechazo
import com.example.reportaciudad.data.model.ReporteServidor
import com.example.reportaciudad.repository.AdminRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminDetalleUiState(
    val reporte: ReporteServidor? = null,
    val cargando: Boolean = true,
    val errorCarga: Boolean = false,
    val guardando: Boolean = false,
    val errorGuardado: Boolean = false,
    val hojaRechazoAbierta: Boolean = false,
    val motivoElegido: MotivoRechazo? = null
)

class AdminDetalleViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: AdminRepository
) : ViewModel() {

    private val remoteId: String =
        checkNotNull(savedStateHandle.get<String>(ARG_REMOTE_ID)) { "Falta el id del reporte" }

    private val _estado = MutableStateFlow(AdminDetalleUiState())
    val estado: StateFlow<AdminDetalleUiState> = _estado.asStateFlow()

    private var carga: Job? = null

    fun cargar() {
        if (_estado.value.guardando) return
        carga?.cancel()
        carga = viewModelScope.launch {
            _estado.update { it.copy(cargando = it.reporte == null, errorCarga = false) }
            repository.reporte(remoteId).fold(
                onSuccess = { reporte -> _estado.update { it.copy(reporte = reporte, cargando = false) } },
                onFailure = { _estado.update { it.copy(cargando = false, errorCarga = true) } }
            )
        }
    }

    fun avanzar() {
        guardar { repository.avanzar(remoteId) }
    }

    fun abrirHojaRechazo() {
        _estado.update { it.copy(hojaRechazoAbierta = true, motivoElegido = null, errorGuardado = false) }
    }

    fun cerrarHojaRechazo() {
        _estado.update { it.copy(hojaRechazoAbierta = false, motivoElegido = null, errorGuardado = false) }
    }

    fun elegirMotivo(motivo: MotivoRechazo) {
        if (_estado.value.guardando) return
        _estado.update { it.copy(motivoElegido = motivo) }
    }

    fun confirmarRechazo() {
        val motivo = _estado.value.motivoElegido ?: return
        guardar { repository.rechazar(remoteId, motivo) }
    }

    private fun guardar(operacion: suspend () -> Result<ReporteServidor>) {
        val actual = _estado.value
        if (actual.guardando || actual.reporte == null) return
        carga?.cancel()
        _estado.update { it.copy(guardando = true, errorGuardado = false) }
        viewModelScope.launch {
            operacion().fold(
                onSuccess = { reporte ->
                    _estado.update {
                        it.copy(
                            reporte = reporte,
                            guardando = false,
                            errorCarga = false,
                            hojaRechazoAbierta = false,
                            motivoElegido = null
                        )
                    }
                },
                onFailure = { _estado.update { it.copy(guardando = false, errorGuardado = true) } }
            )
        }
    }

    companion object {
        const val ARG_REMOTE_ID = "remoteId"
    }
}
