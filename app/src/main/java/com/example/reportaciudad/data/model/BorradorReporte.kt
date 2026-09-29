package com.example.reportaciudad.data.model

import java.io.Serializable

data class BorradorReporte(
    val emergencia: TipoEmergencia? = null,
    val problema: TipoProblema? = null
) : Serializable
