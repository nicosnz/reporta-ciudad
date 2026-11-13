package com.example.reportaciudad.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReporteTest {

    @Test
    fun `un problema de rutina toma titulo, categoria y familia de su tipo`() {
        val reporte = Reporte(id = 1, problema = TipoProblema.BACHE, latitud = -17.78, longitud = -63.18)

        assertEquals("Reporte de bache", reporte.titulo)
        assertEquals("Bache", reporte.categoria)
        assertEquals(Familia.URBANO, reporte.familia)
    }

    @Test
    fun `una emergencia es de la familia EMERGENCIA`() {
        val reporte = Reporte(id = 1, emergencia = TipoEmergencia.INCENDIO, latitud = -17.78, longitud = -63.18)

        assertEquals("Emergencia de incendio", reporte.titulo)
        assertEquals("Incendio", reporte.categoria)
        assertEquals(Familia.EMERGENCIA, reporte.familia)
    }

    @Test
    fun `el lugar son las coordenadas con punto decimal y 5 decimales`() {
        val reporte = Reporte(id = 1, problema = TipoProblema.BASURA, latitud = -17.7836, longitud = -63.1812)

        assertEquals("-17.78360, -63.18120", reporte.lugar)
    }

    @Test
    fun `el formulario esta completo solo con categoria, foto y ubicacion`() {
        val completo = ReporteFormulario(
            problema = TipoProblema.BACHE, fotoRuta = "foto.jpg", latitud = -17.7, longitud = -63.1
        )

        assertTrue(completo.estaCompleto)
        assertFalse(completo.copy(problema = null).estaCompleto)
        assertFalse(completo.copy(fotoRuta = null).estaCompleto)
        assertFalse(completo.copy(latitud = null).estaCompleto)
    }
}
