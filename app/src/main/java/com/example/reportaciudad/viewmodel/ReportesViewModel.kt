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
    // true hasta que Room responde: evita mostrar "no hay reportes" un instante al abrir
    val cargando: Boolean = true
)

// Pantalla "Mis reportes"
class ReportesViewModel(private val repository: ReporteRepository) : ViewModel() {

    val estado: StateFlow<ReportesUiState> = repository.reportes
        .map { ReportesUiState(reportes = it, cargando = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReportesUiState())

    init {
        // Al abrir la app, reenvía lo que quedó sin enviar por falta de conexión
        viewModelScope.launch { repository.reintentarPendientes() }
    }
}
