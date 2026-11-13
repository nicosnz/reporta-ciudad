package com.example.reportaciudad.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.data.model.TipoProblema
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class ReportesViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private val bache = Reporte(id = 1, problema = TipoProblema.BACHE, latitud = -17.7, longitud = -63.1)

    @Test
    fun `al abrir reintenta enviar los pendientes`() {
        val repositorio = FakeReporteRepository()

        ReportesViewModel(repositorio)

        assertEquals(1, repositorio.reintentos)
    }

    @Test
    fun `muestra los reportes del repositorio`() = runTest {
        val repositorio = FakeReporteRepository().apply { lista.value = listOf(bache) }
        val vm = ReportesViewModel(repositorio)

        val estado = vm.estado.first { !it.cargando }

        assertEquals(listOf(bache), estado.reportes)
        assertFalse(estado.cargando)
    }

    @Test
    fun `detalle encuentra el reporte por el id de la ruta`() = runTest {
        val repositorio = FakeReporteRepository().apply { lista.value = listOf(bache) }
        val vm = DetalleViewModel(SavedStateHandle(mapOf(DetalleViewModel.ARG_ID to 1L)), repositorio)

        assertEquals(DetalleUiState.Encontrado(bache), vm.estado.first { it != DetalleUiState.Cargando })
    }

    @Test
    fun `detalle avisa si el reporte no existe`() = runTest {
        val vm = DetalleViewModel(SavedStateHandle(mapOf(DetalleViewModel.ARG_ID to 99L)), FakeReporteRepository())

        assertEquals(DetalleUiState.NoExiste, vm.estado.first { it != DetalleUiState.Cargando })
    }
}
