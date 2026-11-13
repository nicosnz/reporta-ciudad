package com.example.reportaciudad.data.model

import com.example.reportaciudad.util.FechaBolivia
import java.util.Locale

enum class Familia {
    URBANO,
    AMBIENTAL,
    EMERGENCIA
}

// Estado del reporte respecto al servidor: se guarda primero en el teléfono y después se envía
enum class EstadoEnvio {
    PENDIENTE,
    ENVIADO,
    ERROR
}

// Modelo de dominio: guarda datos crudos; los textos que ve el usuario se calculan a partir de ellos.
data class Reporte(
    val id: Long,
    val emergencia: TipoEmergencia? = null,
    val problema: TipoProblema? = null,
    val latitud: Double,
    val longitud: Double,
    val fotoRuta: String? = null,
    val fechaHora: Long = System.currentTimeMillis(),
    val estadoEnvio: EstadoEnvio = EstadoEnvio.PENDIENTE
) {
    val categoria: String get() = emergencia?.nombre ?: problema?.nombre.orEmpty()

    val titulo: String
        get() = if (emergencia != null) "Emergencia de ${categoria.lowercase()}"
        else "Reporte de ${categoria.lowercase()}"

    val familia: Familia
        get() = if (emergencia != null) Familia.EMERGENCIA else problema?.familia ?: Familia.URBANO

    // Locale.US: siempre con punto decimal, como se escriben las coordenadas
    val lugar: String get() = String.format(Locale.US, "%.5f, %.5f", latitud, longitud)

    val fecha: String get() = FechaBolivia.textoEnvio(fechaHora)
    val antiguedad: String get() = FechaBolivia.antiguedad(fechaHora)
}
