package com.example.reportaciudad.data.model

import java.io.Serializable

data class BorradorReporte(
    val emergencia: TipoEmergencia? = null,
    val problema: TipoProblema? = null,
    val fotoRuta: String?=null,
    val latitud: Double? = null,
    val longitud: Double? = null
) : Serializable {
    val estaCompleto: Boolean
        get() = (emergencia != null || problema != null) &&
            fotoRuta != null && latitud != null && longitud != null
}
