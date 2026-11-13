package com.example.reportaciudad.data.local

import com.example.reportaciudad.data.local.db.ReporteEntity
import com.example.reportaciudad.data.model.Reporte

fun ReporteEntity.aDominio(): Reporte = Reporte(
    id = id,
    emergencia = emergencia,
    problema = problema,
    latitud = latitud,
    longitud = longitud,
    fotoRuta = fotoRuta,
    fechaHora = fechaHora,
    estadoEnvio = estadoEnvio
)
