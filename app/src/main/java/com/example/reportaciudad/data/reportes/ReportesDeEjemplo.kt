package com.example.reportaciudad.data.reportes

import com.example.reportaciudad.data.FechaBolivia
import com.example.reportaciudad.data.model.Familia
import com.example.reportaciudad.data.model.Reporte


val reportesDeEjemplo = listOf(
    Reporte(0, "Hueco en Av. Sucre y Junín", "Bache", Familia.URBANO, "Av. Sucre y calle Junín",
        fechaHora = FechaBolivia.instante(2026, 9, 13, 8, 14)),
    Reporte(1, "Basural en la esquina del mercado", "Basura acumulada", Familia.AMBIENTAL, "Mercado Los Pozos",
        fechaHora = FechaBolivia.instante(2026, 6, 26, 17, 40)),
    Reporte(2, "Poste de luz apagado", "Alumbrado", Familia.URBANO, "Calle Libertad y Florida",
        fechaHora = FechaBolivia.instante(2026, 9, 24, 21, 5)),
    Reporte(3, "Fuga de agua en la vereda", "Fuga de agua", Familia.AMBIENTAL, "Av. Cañoto y Ayacucho",
        fechaHora = FechaBolivia.instante(2026, 9, 22, 10, 30))
)
