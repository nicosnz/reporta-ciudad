package com.example.reportaciudad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.repository.ReporteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ReportesUiState(
    val reportes: List<Reporte> = emptyList(),
    val cargando: Boolean = true
)

class ReportesViewModel(private val repository: ReporteRepository) : ViewModel() {

    val estado: StateFlow<ReportesUiState> = repository.reportes
        .map { ReportesUiState(reportes = it, cargando = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReportesUiState())

    init {
        viewModelScope.launch { repository.reintentarPendientes() }
    }

    fun actualizarEstados() {
        viewModelScope.launch { repository.actualizarEstados() }
    }
}
