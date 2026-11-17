package com.example.reportaciudad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportaciudad.data.model.Sesion
import com.example.reportaciudad.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Sesión de toda la app: vive en la Activity, la usa el NavHost para decidir a dónde ir
class SesionViewModel(private val repository: AuthRepository) : ViewModel() {

    val sesion: StateFlow<Sesion?> = repository.sesion
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Espera a que DataStore responda: al abrir la app el StateFlow todavía puede estar en null
    suspend fun sesionGuardada(): Sesion? = repository.sesion.first()

    fun cerrarSesion() {
        viewModelScope.launch { repository.cerrarSesion() }
    }
}
