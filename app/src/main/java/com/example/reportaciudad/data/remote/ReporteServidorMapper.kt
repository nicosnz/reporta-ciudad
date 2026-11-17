package com.example.reportaciudad.data.remote

import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.MotivoRechazo
import com.example.reportaciudad.data.model.ReporteServidor
import com.example.reportaciudad.data.model.TipoEmergencia
import com.example.reportaciudad.data.model.TipoProblema

// JSON de MockAPI → modelo del administrador. Null si el reporte no tiene id (no debería pasar).
fun ReporteDto.aServidor(): ReporteServidor? {
    val remoteId = id ?: return null
    return ReporteServidor(
        remoteId = remoteId,
        emergencia = TipoEmergencia.entries.find { it.name == emergencia },
        problema = TipoProblema.entries.find { it.name == problema },
        latitud = latitud,
        longitud = longitud,
        fechaHora = fechaHora,
        fotoUrl = urlReal(fotoUrl),
        estado = EstadoReporte.desdeTexto(estado),
        motivoRechazo = MotivoRechazo.desdeTexto(motivoRechazo),
        fotoArregloUrl = urlReal(fotoArregloUrl)
    )
}

// Solo una URL de verdad: descarta "" y los textos de relleno de MockAPI ("fotoArregloUrl 1")
private fun urlReal(texto: String?): String? = texto?.trim()?.takeIf { it.startsWith("http") }
