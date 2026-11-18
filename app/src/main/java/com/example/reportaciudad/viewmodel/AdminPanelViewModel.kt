package com.example.reportaciudad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportaciudad.data.model.Bandeja
import com.example.reportaciudad.data.model.ReporteServidor
import com.example.reportaciudad.repository.AdminRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminPanelUiState(
    val bandeja: Bandeja = Bandeja.PENDIENTES,
    val reportes: List<ReporteServidor> = emptyList(),
    val cargando: Boolean = true,
    val error: Boolean = false
) {
    val visibles: List<ReporteServidor> get() = reportes.filter { it.estado.bandeja == bandeja }

    fun cantidad(bandeja: Bandeja): Int = reportes.count { it.estado.bandeja == bandeja }
}

class AdminPanelViewModel(private val repository: AdminRepository) : ViewModel() {

    private val _estado = MutableStateFlow(AdminPanelUiState())
    val estado: StateFlow<AdminPanelUiState> = _estado.asStateFlow()

    private var carga: Job? = null

    fun elegirBandeja(bandeja: Bandeja) {
        _estado.update { it.copy(bandeja = bandeja) }
    }

    fun cargar() {
        carga?.cancel()
        carga = viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = false) }
            repository.reportes().fold(
                onSuccess = { lista -> _estado.update { it.copy(reportes = lista, cargando = false) } },
                onFailure = { _estado.update { it.copy(cargando = false, error = true) } }
            )
        }
    }
}
