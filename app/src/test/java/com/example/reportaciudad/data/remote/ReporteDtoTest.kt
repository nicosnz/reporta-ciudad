package com.example.reportaciudad.data.remote

import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.data.model.TipoEmergencia
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

// Verifica que el JSON coincide con el recurso "reportes" de MockAPI
class ReporteDtoTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `al crear no se manda id y los campos nulos van como null`() {
        val reporte = Reporte(
            id = 7, emergencia = TipoEmergencia.INCENDIO,
            latitud = -17.7836, longitud = -63.1812, fechaHora = 1_794_500_000_000
        )

        val enviado = json.parseToJsonElement(json.encodeToString(ReporteDto.serializer(), reporte.aDto())).jsonObject

        assertFalse("id lo asigna MockAPI", "id" in enviado)
        assertEquals("INCENDIO", enviado["emergencia"]!!.jsonPrimitive.content)
        assertEquals(JsonNull, enviado["problema"])
        assertEquals(JsonNull, enviado["fotoUrl"])
        assertEquals(1_794_500_000_000, enviado["fechaHora"]!!.jsonPrimitive.long)
    }

    @Test
    fun `lee la respuesta de MockAPI ignorando campos extra`() {
        val respuesta = """
            {"id":"12","emergencia":null,"problema":"BACHE","latitud":-17.7,"longitud":-63.1,
             "fechaHora":1794500000000,"fotoUrl":null,"createdAt":"2026-11-13T12:00:00.000Z"}
        """.trimIndent()

        val dto = json.decodeFromString(ReporteDto.serializer(), respuesta)

        assertEquals("12", dto.id)
        assertEquals("BACHE", dto.problema)
    }
}
