package com.example.reportaciudad.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.reportaciudad.AppContainer
import com.example.reportaciudad.ReportaCiudadApp
import com.example.reportaciudad.repository.ReporteRepository

object AppViewModelProvider {

    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { ReportesViewModel(repositorio()) }
        initializer { DetalleViewModel(createSavedStateHandle(), repositorio()) }
        initializer { ReporteFormularioViewModel(repositorio()) }
        initializer { LoginViewModel(contenedor().authRepository) }
        initializer { SesionViewModel(contenedor().authRepository) }
        initializer { AdminPanelViewModel(contenedor().adminRepository) }
        initializer { AdminDetalleViewModel(createSavedStateHandle(), contenedor().adminRepository) }
        initializer { AdminFotoArregloViewModel(createSavedStateHandle(), contenedor().adminRepository) }
    }

    private fun CreationExtras.contenedor(): AppContainer =
        (this[APPLICATION_KEY] as ReportaCiudadApp).container

    private fun CreationExtras.repositorio(): ReporteRepository = contenedor().reporteRepository
}
