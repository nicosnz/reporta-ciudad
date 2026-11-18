package com.example.reportaciudad.repository

import com.example.reportaciudad.data.local.AlmacenFotos
import com.example.reportaciudad.data.local.UbicacionActual
import com.example.reportaciudad.data.local.aDominio
import com.example.reportaciudad.data.local.db.ReporteDao
import com.example.reportaciudad.data.local.db.ReporteEntity
import com.example.reportaciudad.data.model.EstadoEnvio
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.data.model.ReporteFormulario
import com.example.reportaciudad.data.model.Ubicacion
import com.example.reportaciudad.data.remote.FotoRemoteDataSource
import com.example.reportaciudad.data.remote.ReporteRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

// Único punto de acceso a los datos para los ViewModels.
// Room es la fuente de verdad: la UI siempre lee de la base local, el servidor solo recibe.
class ReporteRepository(
    private val dao: ReporteDao,
    private val remoto: ReporteRemoteDataSource,
    private val fotos: FotoRemoteDataSource,
    private val almacenFotos: AlmacenFotos,
    private val ubicacionActual: UbicacionActual,
    private val ahora: () -> Long = System::currentTimeMillis
) {

    // Evita mandar el mismo reporte dos veces si se cruzan un envío y un reintento
    private val sincronizando = Mutex()

    val reportes: Flow<List<Reporte>> =
        dao.observarTodos().map { lista -> lista.map { it.aDominio() } }

    fun reporte(id: Long): Flow<Reporte?> =
        dao.observarPorId(id).map { it?.aDominio() }

    // Guarda el reporte en el teléfono y lo intenta enviar al servidor.
    // Devuelve cómo quedó: ENVIADO, o ERROR si no hubo conexión (se reintenta después).
    suspend fun enviar(formulario: ReporteFormulario): EstadoEnvio {
        require(formulario.estaCompleto) { "El reporte no está completo" }
        val nuevo = ReporteEntity(
            emergencia = formulario.emergencia,
            problema = formulario.problema,
            latitud = checkNotNull(formulario.latitud),
            longitud = checkNotNull(formulario.longitud),
            fotoRuta = formulario.fotoRuta,
            fechaHora = ahora(),
            estadoEnvio = EstadoEnvio.PENDIENTE
        )
        // 1) Primero en el teléfono: aparece en la lista aunque no haya señal
        val id = dao.insertar(nuevo)
        // 2) Después al servidor
        return sincronizando.withLock {
            // Mientras esperaba, un reintento pudo haberlo enviado ya: no se manda otra vez
            if (dao.estadoEnvio(id) == EstadoEnvio.ENVIADO) EstadoEnvio.ENVIADO
            else sincronizar(nuevo.copy(id = id))
        }
    }

    // Reenvía los reportes que quedaron sin enviar
    suspend fun reintentarPendientes() {
        if (!sincronizando.tryLock()) return     // ya hay una sincronización en curso
        try {
            dao.pendientes().forEach { sincronizar(it) }
        } finally {
            sincronizando.unlock()
        }
    }

    // Trae del servidor el estado de los reportes enviados que todavía pueden cambiar.
    // Sin conexión no hace nada: se sigue viendo el último estado conocido.
    suspend fun actualizarEstados() {
        dao.enviadosAbiertos().forEach { actualizarDesdeServidor(it) }
    }

    // Lo mismo para un solo reporte (al abrir su detalle)
    suspend fun actualizarEstado(id: Long) {
        val reporte = dao.porId(id) ?: return
        if (reporte.estadoEnvio == EstadoEnvio.ENVIADO && !reporte.estado.esFinal) actualizarDesdeServidor(reporte)
    }

    private suspend fun actualizarDesdeServidor(reporte: ReporteEntity) {
        val remoteId = reporte.remoteId ?: return
        remoto.seguimiento(remoteId).onSuccess { s ->
            dao.actualizarEstado(reporte.id, s.estado, s.motivoRechazo, s.fotoArregloUrl)
        }
    }

    // 1) Sube la foto a Cloudinary (si todavía no se subió)  2) Manda el reporte a MockAPI con su URL
    private suspend fun sincronizar(reporte: ReporteEntity): EstadoEnvio {
        val fotoUrl = reporte.fotoUrl ?: subirFoto(reporte).getOrElse { return marcarError(reporte) }
        return remoto.enviar(reporte.copy(fotoUrl = fotoUrl).aDominio()).fold(
            onSuccess = { remoteId ->
                dao.actualizarEnvio(reporte.id, EstadoEnvio.ENVIADO, remoteId)
                EstadoEnvio.ENVIADO
            },
            onFailure = { marcarError(reporte) }
        )
    }

    // URL de la foto subida, o null si el reporte no tiene foto (o el archivo ya no está).
    // Se guarda enseguida en Room: si después falla el envío, el reintento no la vuelve a subir.
    private suspend fun subirFoto(reporte: ReporteEntity): Result<String?> {
        val archivo = reporte.fotoRuta?.let(::File)?.takeIf { it.exists() } ?: return Result.success(null)
        return fotos.subir(archivo).onSuccess { url -> dao.actualizarFotoUrl(reporte.id, url) }
    }

    private suspend fun marcarError(reporte: ReporteEntity): EstadoEnvio {
        dao.actualizarEnvio(reporte.id, EstadoEnvio.ERROR, null)
        return EstadoEnvio.ERROR
    }

    fun nuevoArchivoFoto(): File = almacenFotos.crearArchivo()

    fun descartarFoto(ruta: String?) = almacenFotos.borrar(ruta)

    suspend fun obtenerUbicacion(): Ubicacion? = ubicacionActual.obtener()
}
