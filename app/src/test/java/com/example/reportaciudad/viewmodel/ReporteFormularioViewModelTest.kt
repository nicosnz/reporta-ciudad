package com.example.reportaciudad.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.reportaciudad.data.model.EstadoEnvio
import com.example.reportaciudad.data.model.TipoEmergencia
import com.example.reportaciudad.data.model.TipoProblema
import com.example.reportaciudad.data.model.Ubicacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReporteFormularioViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private lateinit var repositorio: FakeReporteRepository
    private lateinit var store: ViewModelStore
    private lateinit var vm: ReporteFormularioViewModel

    @Before
    fun preparar() {
        repositorio = FakeReporteRepository()
        // Se crea con un ViewModelStore para poder simular la salida del flujo (store.clear())
        store = ViewModelStore()
        val factory = viewModelFactory { initializer { ReporteFormularioViewModel(repositorio) } }
        vm = ViewModelProvider.create(store, factory)[ReporteFormularioViewModel::class]
    }

    private fun completarFormulario() {
        vm.elegirProblema(TipoProblema.BACHE)
        vm.fotoSacada("foto1.jpg")
        vm.buscarUbicacion()
        vm.confirmarUbicacion()
    }

    @Test
    fun `elegir una emergencia borra el problema elegido antes`() {
        vm.elegirProblema(TipoProblema.BASURA)
        vm.elegirEmergencia(TipoEmergencia.HUMO)

        val borrador = vm.estado.value.borrador
        assertEquals(TipoEmergencia.HUMO, borrador.emergencia)
        assertNull(borrador.problema)
    }

    @Test
    fun `sacar otra foto borra la anterior`() {
        vm.fotoSacada("foto1.jpg")
        vm.fotoSacada("foto2.jpg")

        assertEquals("foto2.jpg", vm.estado.value.borrador.fotoRuta)
        assertEquals(listOf("foto1.jpg"), repositorio.fotosBorradas)
    }

    @Test
    fun `repetir la foto la borra del telefono`() {
        vm.fotoSacada("foto1.jpg")
        vm.descartarFoto()

        assertNull(vm.estado.value.borrador.fotoRuta)
        assertEquals(listOf("foto1.jpg"), repositorio.fotosBorradas)
    }

    @Test
    fun `la ubicacion del GPS solo pasa al borrador al confirmarla`() {
        vm.buscarUbicacion()

        assertEquals(Ubicacion(-17.7836, -63.1812), vm.estado.value.ubicacion)
        assertNull(vm.estado.value.borrador.latitud)

        vm.confirmarUbicacion()

        assertEquals(-17.7836, vm.estado.value.borrador.latitud!!, 0.0)
        assertEquals(-63.1812, vm.estado.value.borrador.longitud!!, 0.0)
    }

    @Test
    fun `sin GPS avisa que no encontro la ubicacion`() {
        repositorio.ubicacionDelGps = null

        vm.buscarUbicacion()

        assertTrue(vm.estado.value.sinUbicacion)
        assertNull(vm.estado.value.ubicacion)
    }

    @Test
    fun `no envia un formulario incompleto`() {
        vm.elegirProblema(TipoProblema.BACHE)
        vm.enviar()

        assertTrue(repositorio.enviados.isEmpty())
        assertEquals(Envio.INACTIVO, vm.estado.value.envio)
    }

    @Test
    fun `envia una sola vez aunque se toque dos veces`() {
        completarFormulario()

        vm.enviar()
        vm.enviar()

        assertEquals(1, repositorio.enviados.size)
        assertEquals(Envio.ENVIADO, vm.estado.value.envio)
    }

    @Test
    fun `sin conexion queda guardado para enviar despues`() {
        repositorio.resultadoEnvio = EstadoEnvio.ERROR
        completarFormulario()

        vm.enviar()

        assertEquals(Envio.GUARDADO_SIN_CONEXION, vm.estado.value.envio)
    }

    @Test
    fun `salir del flujo sin enviar borra la foto`() {
        completarFormulario()

        store.clear()

        assertEquals(listOf("foto1.jpg"), repositorio.fotosBorradas)
    }

    @Test
    fun `salir despues de enviar conserva la foto`() {
        completarFormulario()
        vm.enviar()

        store.clear()

        assertTrue(repositorio.fotosBorradas.isEmpty())
    }
}
