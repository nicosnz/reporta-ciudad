package com.example.reportaciudad.data.model

enum class EstadoReporte(val nombre: String, val paso: Int) {
    RECIBIDO("Recibido", 1),
    EN_REVISION("En revisión", 2),
    ASIGNADO("Asignado", 3),
    RESUELTO("Resuelto", 4),
    RECHAZADO("Rechazado", 4),
    CANCELADO("Cancelado", 4);

    val esFinal: Boolean get() = this == RESUELTO || this == RECHAZADO || this == CANCELADO

    val vecinoPuedeCancelar: Boolean get() = this == RECIBIDO

    val siguiente: EstadoReporte?
        get() = when (this) {
            RECIBIDO -> EN_REVISION
            EN_REVISION -> ASIGNADO
            ASIGNADO -> RESUELTO
            RESUELTO, RECHAZADO, CANCELADO -> null
        }

    val bandeja: Bandeja
        get() = when (this) {
            RECIBIDO, EN_REVISION -> Bandeja.PENDIENTES
            ASIGNADO -> Bandeja.EN_CURSO
            RESUELTO, RECHAZADO, CANCELADO -> Bandeja.CERRADOS
        }

    companion object {
        fun desdeTexto(texto: String?): EstadoReporte =
            entries.find { it.name == texto?.trim()?.uppercase() } ?: RECIBIDO
    }
}

enum class MotivoRechazo(val nombre: String, val explicacion: String) {
    DUPLICADO("Duplicado", "Ya había otro reporte del mismo problema, que se está atendiendo."),
    NO_CORRESPONDE("No corresponde a la municipalidad", "Este problema lo atiende otra institución."),
    NO_ENCONTRADO("No se encontró el problema", "Cuando fueron al lugar, no encontraron el problema."),
    OTRO("Otro motivo", "");

    companion object {
        fun desdeTexto(texto: String?): MotivoRechazo? {
            val limpio = texto?.trim()?.uppercase()
            if (limpio.isNullOrEmpty()) return null
            return entries.find { it.name == limpio } ?: OTRO
        }
    }
}
