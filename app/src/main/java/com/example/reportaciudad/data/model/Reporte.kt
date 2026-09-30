package com.example.reportaciudad.data.model

import com.example.reportaciudad.data.FechaBolivia

enum class Familia { URBANO, AMBIENTAL }

data class Reporte(
    val id: Int,
    val titulo: String,
    val categoria: String,
    val familia: Familia,
    val lugar: String,

    val fotoRuta: String? = null,

    val fechaHora: Long = System.currentTimeMillis()
) {

    val fecha: String get() = FechaBolivia.textoEnvio(fechaHora)
    val antiguedad: String get() = FechaBolivia.antiguedad(fechaHora)
}
