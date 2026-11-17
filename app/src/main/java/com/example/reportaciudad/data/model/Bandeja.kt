package com.example.reportaciudad.data.model

// Pestañas del panel del administrador
enum class Bandeja(val nombre: String) {
    PENDIENTES("Pendientes"),   // recibidos y en revisión
    EN_CURSO("En curso"),       // asignados a una cuadrilla
    CERRADOS("Cerrados")        // resueltos y rechazados
}
