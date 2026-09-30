package com.example.reportaciudad.data.reportes

import com.example.reportaciudad.data.model.BorradorReporte
import com.example.reportaciudad.data.model.Reporte

// Crea el reporte a partir del borrador y lo agrega al principio de la lista.
fun enviarReporte(borrador: BorradorReporte): Reporte {
    require(borrador.estaCompleto) { "El reporte no está completo" }
    val nuevo = crearReporte(borrador, id = (reportes.maxOfOrNull { it.id } ?: -1) + 1)
    reportes.add(0, nuevo)
    return nuevo
}
