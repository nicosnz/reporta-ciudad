package com.example.reportaciudad.data.reportes

import com.example.reportaciudad.data.model.ReporteFormulario
import com.example.reportaciudad.data.model.Reporte


fun enviarReporte(borrador: ReporteFormulario): Reporte {
    require(borrador.estaCompleto) { "El reporte no está completo" }
    val nuevo = crearReporte(borrador, id = (reportes.maxOfOrNull { it.id } ?: -1) + 1)
    reportes.add(0, nuevo)
    return nuevo
}
