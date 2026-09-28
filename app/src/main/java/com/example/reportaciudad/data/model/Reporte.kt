package com.example.reportaciudad.data.model

enum class Familia { URBANO, AMBIENTAL }

data class Reporte(
    val id: Int,
    val titulo: String,
    val categoria: String,
    val antiguedad: String,
    val familia: Familia,
    val fecha: String,
    val lugar: String
)
