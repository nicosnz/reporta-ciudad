package com.example.reportaciudad.data.model

data class ReporteFormulario(
    val emergencia: TipoEmergencia? = null,
    val problema: TipoProblema? = null,
    val fotoRuta: String?=null,
    val latitud: Double? = null,
    val longitud: Double? = null
) {
    val estaCompleto: Boolean
        get() = (emergencia != null || problema != null) &&
            fotoRuta != null && latitud != null && longitud != null
}
