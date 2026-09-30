package com.example.reportaciudad.data

import androidx.compose.runtime.mutableStateListOf
import com.example.reportaciudad.data.model.BorradorReporte
import com.example.reportaciudad.data.model.Familia
import com.example.reportaciudad.data.model.Reporte
import kotlinx.coroutines.delay
import java.util.Locale

// Reportes del usuario. Por ahora se guardan solo en memoria: al enviar uno, se agrega a la lista.
// Cuando exista la API, enviar() subirá el reporte al servidor; las pantallas no cambian.
object RepositorioReportes {

    private const val ESPERA_SIMULADA_MS = 900L

    // Reportes de ejemplo del diseño, con su fecha real en hora de Bolivia
    private val ejemplos = listOf(
        Reporte(0, "Hueco en Av. Sucre y Junín", "Bache", Familia.URBANO, "Av. Sucre y calle Junín",
            fechaHora = FechaBolivia.instante(2026, 9, 13, 8, 14)),
        Reporte(1, "Basural en la esquina del mercado", "Basura acumulada", Familia.AMBIENTAL, "Mercado Los Pozos",
            fechaHora = FechaBolivia.instante(2026, 6, 26, 17, 40)),
        Reporte(2, "Poste de luz apagado", "Alumbrado", Familia.URBANO, "Calle Libertad y Florida",
            fechaHora = FechaBolivia.instante(2026, 9, 24, 21, 5)),
        Reporte(3, "Fuga de agua en la vereda", "Fuga de agua", Familia.AMBIENTAL, "Av. Cañoto y Ayacucho",
            fechaHora = FechaBolivia.instante(2026, 9, 22, 10, 30))
    )

    // Lista observable: Mis reportes se actualiza sola cuando se agrega uno
    private val _reportes = mutableStateListOf<Reporte>().apply { addAll(ejemplos) }
    val reportes: List<Reporte> get() = _reportes

    fun buscar(id: Int): Reporte? = _reportes.firstOrNull { it.id == id }

    // Envía el reporte (simulado) y lo agrega al principio de la lista. La fecha y hora se ponen solas.
    suspend fun enviar(borrador: BorradorReporte): Reporte {
        require(borrador.estaCompleto) { "El reporte no está completo" }
        // TODO: subir la foto (borrador.fotoRuta) y los datos del reporte al servidor
        delay(ESPERA_SIMULADA_MS)
        val nuevo = borrador.aReporte(id = (_reportes.maxOfOrNull { it.id } ?: -1) + 1)
        _reportes.add(0, nuevo)
        return nuevo
    }

    private fun BorradorReporte.aReporte(id: Int): Reporte {
        // Todavía no se obtiene el nombre de la calle: el lugar son las coordenadas
        val lugar = String.format(Locale.US, "%.5f, %.5f", latitud, longitud)
        emergencia?.let {
            // Incendio y humo se muestran con los colores ambientales
            return Reporte(id, "Emergencia de ${it.nombre.lowercase()}", it.nombre, Familia.AMBIENTAL, lugar, fotoRuta)
        }
        val tipo = checkNotNull(problema)
        return Reporte(id, "Reporte de ${tipo.nombre.lowercase()}", tipo.nombre, tipo.familia, lugar, fotoRuta)
    }
}
