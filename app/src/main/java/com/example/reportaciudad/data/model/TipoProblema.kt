package com.example.reportaciudad.data.model

enum class TipoProblema(val nombre: String, val familia: Familia) {
    BASURA("Basura", Familia.AMBIENTAL),
    BACHE("Bache", Familia.URBANO),
    ALUMBRADO("Alumbrado", Familia.URBANO),
    FUGA_DE_AGUA("Fuga de agua", Familia.AMBIENTAL)
}
