package com.example.reportaciudad.data.reportes

import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.data.model.ReporteFormulario


fun crearReporte(borrador: ReporteFormulario, id: Long): Reporte = Reporte(
    id = id,
    emergencia = borrador.emergencia,
    problema = borrador.problema,
    latitud = checkNotNull(borrador.latitud),
    longitud = checkNotNull(borrador.longitud),
    fotoRuta = borrador.fotoRuta
)
