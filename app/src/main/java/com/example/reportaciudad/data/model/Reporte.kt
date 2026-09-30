package com.example.reportaciudad.data.model

import com.example.reportaciudad.data.FechaBolivia

enum class Familia { URBANO, AMBIENTAL }

data class Reporte(
    val id: Int,
    val titulo: String,
    val categoria: String,
    val familia: Familia,
    val lugar: String,
    // Foto sacada con la cámara; null en los reportes de ejemplo (usan la foto de muestra)
    val fotoRuta: String? = null,
    // Momento en que se creó el reporte: se completa solo. Se muestra en hora de Bolivia.
    val fechaHora: Long = System.currentTimeMillis()
) {
    // "el 13 de septiembre · 08:14"
    val fecha: String get() = FechaBolivia.textoEnvio(fechaHora)

    // "hace 14 días", calculado cada vez respecto de ahora
    val antiguedad: String get() = FechaBolivia.antiguedad(fechaHora)
}
