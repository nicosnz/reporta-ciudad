package com.example.reportaciudad.data.reportes

import com.example.reportaciudad.data.model.BorradorReporte
import com.example.reportaciudad.data.model.Familia
import com.example.reportaciudad.data.model.Reporte
import java.util.Locale


fun crearReporte(borrador: BorradorReporte, id: Int): Reporte {

    val lugar = String.format(Locale.US, "%.5f, %.5f", borrador.latitud, borrador.longitud)

    borrador.emergencia?.let {
        return Reporte(id, "Emergencia de ${it.nombre.lowercase()}", it.nombre, Familia.AMBIENTAL, lugar, borrador.fotoRuta)
    }
    val tipo = checkNotNull(borrador.problema) { "El borrador no tiene categoría" }
    return Reporte(id, "Reporte de ${tipo.nombre.lowercase()}", tipo.nombre, tipo.familia, lugar, borrador.fotoRuta)
}
