package com.example.reportaciudad.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.repository.ReporteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data class Encontrado(val reporte: Reporte) : DetalleUiState
    data object NoExiste : DetalleUiState
}

// Pantalla Detalle. El id llega como argumento de la ruta (Detalle(id)) en el SavedStateHandle.
class DetalleViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ReporteRepository
) : ViewModel() {

    private val id: Long = checkNotNull(savedStateHandle.get<Long>(ARG_ID)) { "Falta el id del reporte" }

    val estado: StateFlow<DetalleUiState> = repository.reporte(id)
        .map { reporte -> reporte?.let { DetalleUiState.Encontrado(it) } ?: DetalleUiState.NoExiste }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalleUiState.Cargando)

    init {
        // Al abrir el detalle se pide el estado actual; mientras tanto se ve el que había en Room
        viewModelScope.launch { repository.actualizarEstado(id) }
    }

    companion object {
        // Nombre de la propiedad en la ruta Detalle(val id: Long)
        const val ARG_ID = "id"
    }
}
