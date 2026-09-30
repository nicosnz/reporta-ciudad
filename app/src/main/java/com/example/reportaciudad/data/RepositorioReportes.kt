package com.example.reportaciudad.data

import com.example.reportaciudad.data.model.BorradorReporte
import kotlinx.coroutines.delay

// Envío de reportes. Todavía no hay servidor: se simula la espera de la red.
// Cuando exista la API, solo cambia el cuerpo de enviar(); las pantallas no se tocan.
object RepositorioReportes {

    private const val ESPERA_SIMULADA_MS = 900L

    suspend fun enviar(borrador: BorradorReporte) {
        require(borrador.estaCompleto) { "El reporte no está completo" }
        // TODO: subir la foto (borrador.fotoRuta) y los datos del reporte al servidor
        delay(ESPERA_SIMULADA_MS)
    }
}
