package com.example.reportaciudad.data.reportes

import com.example.reportaciudad.data.model.Reporte

fun buscarReporte(id: Int): Reporte? = reportes.filter { it.id == id }.firstOrNull()
