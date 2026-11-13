package com.example.reportaciudad.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FechaBoliviaTest {

    private val ahora = FechaBolivia.instante(2026, 11, 13, 12, 0)
    private val minuto = 60_000L

    @Test
    fun `antiguedad en minutos, horas y dias`() {
        assertEquals("recién", FechaBolivia.antiguedad(ahora - 30_000, ahora))
        assertEquals("hace 5 min", FechaBolivia.antiguedad(ahora - 5 * minuto, ahora))
        assertEquals("hace 3 h", FechaBolivia.antiguedad(ahora - 3 * 60 * minuto, ahora))
        assertEquals("hace 1 día", FechaBolivia.antiguedad(ahora - 30 * 60 * minuto, ahora))
        assertEquals("hace 17 días", FechaBolivia.antiguedad(ahora - 17 * 24 * 60 * minuto, ahora))
    }

    @Test
    fun `una fecha futura cuenta como recien`() {
        assertEquals("recién", FechaBolivia.antiguedad(ahora + 10 * minuto, ahora))
    }

    @Test
    fun `el texto de envio usa la hora de Bolivia`() {
        // 16:30 UTC = 12:30 en Bolivia (UTC-4)
        val instanteUtc = FechaBolivia.instante(2026, 11, 13, 12, 30)

        assertEquals("el 13 de noviembre · 12:30", FechaBolivia.textoEnvio(instanteUtc))
    }
}
