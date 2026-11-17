package com.example.reportaciudad.data.model

import com.example.reportaciudad.util.FechaBolivia
import java.util.Locale

enum class Familia {
    URBANO,
    AMBIENTAL,
    EMERGENCIA
}


enum class EstadoEnvio {
    PENDIENTE,
    ENVIADO,
    ERROR
}


data class Reporte(
    val id: Long,
    val emergencia: TipoEmergencia? = null,
    val problema: TipoProblema? = null,
    val latitud: Double,
    val longitud: Double,
    val fotoRuta: String? = null,
    // URL de la foto en Cloudinary; null mientras no se haya subido
    val fotoUrl: String? = null,
    val fechaHora: Long = System.currentTimeMillis(),
    val estadoEnvio: EstadoEnvio = EstadoEnvio.PENDIENTE,
    val estado: EstadoReporte = EstadoReporte.RECIBIDO,
    val motivoRechazo: MotivoRechazo? = null,
    // Foto que sube la cuadrilla cuando lo arregla
    val fotoArregloUrl: String? = null
) {
    // El estado solo tiene sentido si el reporte llegó al servidor
    val enviado: Boolean get() = estadoEnvio == EstadoEnvio.ENVIADO

    // La copia del teléfono carga sin internet; si ya no está, la del servidor
    val foto: String? get() = fotoRuta ?: fotoUrl

    val categoria: String get() = emergencia?.nombre ?: problema?.nombre.orEmpty()

    val titulo: String
        get() = if (emergencia != null) "Emergencia de ${categoria.lowercase()}"
        else "Reporte de ${categoria.lowercase()}"

    val familia: Familia
        get() = if (emergencia != null) Familia.EMERGENCIA else problema?.familia ?: Familia.URBANO


    val lugar: String get() = String.format(Locale.US, "%.5f, %.5f", latitud, longitud)

    val fecha: String get() = FechaBolivia.textoEnvio(fechaHora)
    val antiguedad: String get() = FechaBolivia.antiguedad(fechaHora)
}
