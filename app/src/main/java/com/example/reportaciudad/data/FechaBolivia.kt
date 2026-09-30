package com.example.reportaciudad.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Fechas de los reportes en hora de Bolivia (UTC-4, sin horario de verano), sin importar
// la zona horaria configurada en el teléfono. Se guarda el instante (milisegundos) y se
// convierte a la hora boliviana solo al mostrarlo.
object FechaBolivia {

    val ZONA: TimeZone = TimeZone.getTimeZone("America/La_Paz")
    private val ESPANOL: Locale = Locale.forLanguageTag("es-BO")

    private const val MINUTO_MS = 60_000L
    private const val HORA_MIN = 60L
    private const val DIA_MIN = 24 * HORA_MIN

    // "el 13 de septiembre · 08:14"
    fun textoEnvio(instante: Long): String {
        val formato = SimpleDateFormat("d 'de' MMMM · HH:mm", ESPANOL).apply { timeZone = ZONA }
        return "el ${formato.format(Date(instante))}"
    }

    // "recién", "hace 5 min", "hace 3 h", "hace 1 día", "hace 14 días"
    fun antiguedad(instante: Long, ahora: Long = System.currentTimeMillis()): String {
        val minutos = (ahora - instante).coerceAtLeast(0) / MINUTO_MS
        return when {
            minutos < 1 -> "recién"
            minutos < HORA_MIN -> "hace $minutos min"
            minutos < DIA_MIN -> "hace ${minutos / HORA_MIN} h"
            minutos < 2 * DIA_MIN -> "hace 1 día"
            else -> "hace ${minutos / DIA_MIN} días"
        }
    }

    // Instante de una fecha y hora dadas en hora de Bolivia (mes de 1 a 12)
    fun instante(anio: Int, mes: Int, dia: Int, hora: Int, minuto: Int): Long =
        Calendar.getInstance(ZONA).apply {
            clear()
            set(anio, mes - 1, dia, hora, minuto)
        }.timeInMillis
}
