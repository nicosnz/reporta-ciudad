package com.example.reportaciudad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportaciudad.data.model.Sesion
import com.example.reportaciudad.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SesionViewModel(private val repository: AuthRepository) : ViewModel() {

    val sesion: StateFlow<Sesion?> = repository.sesion
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val haySesion: StateFlow<Boolean?> = repository.sesion
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    suspend fun sesionGuardada(): Sesion? = repository.sesion.first()

    fun cerrarSesion() {
        viewModelScope.launch { repository.cerrarSesion() }
    }
}
