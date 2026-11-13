package com.example.reportaciudad.data.reportes

import com.example.reportaciudad.data.model.Reporte

fun buscarReporte(id: Long): Reporte? = reportes.firstOrNull { it.id == id }
