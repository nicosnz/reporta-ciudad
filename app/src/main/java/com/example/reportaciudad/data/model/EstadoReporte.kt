package com.example.reportaciudad.data.model

// En qué va la municipalidad con el reporte. Lo cambia el administrador en el servidor.
// paso: cuántos de los 4 segmentos de la barra se marcan.
enum class EstadoReporte(val nombre: String, val paso: Int) {
    RECIBIDO("Recibido", 1),
    EN_REVISION("En revisión", 2),
    ASIGNADO("Asignado", 3),
    RESUELTO("Resuelto", 4),
    RECHAZADO("Rechazado", 4);

    // Resuelto y rechazado ya no cambian: no hace falta volver a consultarlos
    val esFinal: Boolean get() = this == RESUELTO || this == RECHAZADO

    companion object {
        // Un reporte viejo sin estado, o con un valor desconocido, cuenta como recién recibido
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
        // Vacío: sin motivo. Un valor desconocido se muestra como "Otro motivo".
        fun desdeTexto(texto: String?): MotivoRechazo? {
            val limpio = texto?.trim()?.uppercase()
            if (limpio.isNullOrEmpty()) return null
            return entries.find { it.name == limpio } ?: OTRO
        }
    }
}
