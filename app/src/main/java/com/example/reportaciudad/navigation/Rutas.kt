package com.example.reportaciudad.navigation

object Ruta {
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
    const val ADMIN = "admin"
    const val ADMIN_DETALLE = "admin/detalle/{remoteId}"
    const val ADMIN_FOTO_ARREGLO = "admin/foto-arreglo/{remoteId}"
    const val INICIO = "inicio"
    const val DETALLE = "detalle/{id}"

    const val FLUJO_REPORTAR = "reportar"
    const val PASO_TIPO = "reportar/tipo"
    const val PASO_EMERGENCIA = "reportar/emergencia"
    const val PASO_PROBLEMA = "reportar/problema"
    const val PASO_FOTOGRAFIA = "reportar/fotografia"
    const val PASO_LOCALIZACION = "reportar/localizacion"
    const val PASO_RESUMEN = "reportar/resumen"

    fun adminDetalle(remoteId: String) = "admin/detalle/$remoteId"
    fun adminFotoArreglo(remoteId: String) = "admin/foto-arreglo/$remoteId"
    fun detalle(id: Long) = "detalle/$id"
}
