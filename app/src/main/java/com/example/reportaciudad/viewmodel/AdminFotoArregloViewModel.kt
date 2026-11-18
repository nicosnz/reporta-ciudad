package com.example.reportaciudad.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportaciudad.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class AdminFotoArregloUiState(
    val fotoAntes: String? = null,
    val fotoRuta: String? = null,
    val subiendo: Boolean = false,
    val error: Boolean = false,
    val resuelto: Boolean = false
)

class AdminFotoArregloViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: AdminRepository
) : ViewModel() {

    private val remoteId: String =
        checkNotNull(savedStateHandle.get<String>(AdminDetalleViewModel.ARG_REMOTE_ID)) { "Falta el id del reporte" }

    private val _estado = MutableStateFlow(AdminFotoArregloUiState())
    val estado: StateFlow<AdminFotoArregloUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            repository.reporte(remoteId).onSuccess { reporte ->
                _estado.update { it.copy(fotoAntes = reporte.fotoUrl) }
            }
        }
    }

    fun nuevoArchivoFoto(): File = repository.nuevoArchivoFoto()

    fun fotoSacada(ruta: String) {
        reemplazarFoto(ruta)
    }

    fun fotoElegida(uri: Uri) {
        viewModelScope.launch {
            repository.importarFoto(uri)?.let { reemplazarFoto(it.absolutePath) }
        }
    }

    fun repetir() {
        if (_estado.value.subiendo) return
        repository.descartarFoto(_estado.value.fotoRuta)
        _estado.update { it.copy(fotoRuta = null, error = false) }
    }

    fun confirmar() {
        val actual = _estado.value
        val ruta = actual.fotoRuta ?: return
        if (actual.subiendo || actual.resuelto) return
        _estado.update { it.copy(subiendo = true, error = false) }
        viewModelScope.launch {
            repository.resolver(remoteId, File(ruta)).fold(
                onSuccess = {
                    repository.descartarFoto(ruta)
                    _estado.update { it.copy(subiendo = false, resuelto = true, fotoRuta = null) }
                },
                onFailure = { _estado.update { it.copy(subiendo = false, error = true) } }
            )
        }
    }

    private fun reemplazarFoto(ruta: String) {
        val anterior = _estado.value.fotoRuta
        if (anterior != null && anterior != ruta) repository.descartarFoto(anterior)
        _estado.update { it.copy(fotoRuta = ruta, error = false) }
    }

    override fun onCleared() {
        repository.descartarFoto(_estado.value.fotoRuta)
    }
}
