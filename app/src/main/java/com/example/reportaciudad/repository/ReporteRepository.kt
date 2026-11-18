package com.example.reportaciudad.repository

import com.example.reportaciudad.data.local.AlmacenFotos
import com.example.reportaciudad.data.local.UbicacionActual
import com.example.reportaciudad.data.local.aDominio
import com.example.reportaciudad.data.local.db.ReporteDao
import com.example.reportaciudad.data.local.db.ReporteEntity
import com.example.reportaciudad.data.model.EstadoEnvio
import com.example.reportaciudad.data.model.EstadoReporte
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

enum class ResultadoCancelacion {
    ELIMINADO,
    CANCELADO,
    NO_PERMITIDO,
    SIN_CONEXION
}

class ReporteRepository(
    private val dao: ReporteDao,
    private val remoto: ReporteRemoteDataSource,
    private val fotos: FotoRemoteDataSource,
    private val almacenFotos: AlmacenFotos,
    private val ubicacionActual: UbicacionActual,
    private val ahora: () -> Long = System::currentTimeMillis
) {

    private val sincronizando = Mutex()

    val reportes: Flow<List<Reporte>> =
        dao.observarTodos().map { lista -> lista.map { it.aDominio() } }

    fun reporte(id: Long): Flow<Reporte?> =
        dao.observarPorId(id).map { it?.aDominio() }

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
        val id = dao.insertar(nuevo)
        return sincronizando.withLock {
            if (dao.estadoEnvio(id) == EstadoEnvio.ENVIADO) EstadoEnvio.ENVIADO
            else sincronizar(nuevo.copy(id = id))
        }
    }

    suspend fun reintentarPendientes() {
        if (!sincronizando.tryLock()) return
        try {
            dao.pendientes().forEach { sincronizar(it) }
        } finally {
            sincronizando.unlock()
        }
    }

    suspend fun actualizarEstados() {
        dao.enviadosAbiertos().forEach { actualizarDesdeServidor(it) }
    }

    suspend fun actualizarEstado(id: Long) {
        val reporte = dao.porId(id) ?: return
        if (reporte.estadoEnvio == EstadoEnvio.ENVIADO && !reporte.estado.esFinal) actualizarDesdeServidor(reporte)
    }

    suspend fun cancelar(id: Long): ResultadoCancelacion = sincronizando.withLock { cancelarBloqueado(id) }

    private suspend fun cancelarBloqueado(id: Long): ResultadoCancelacion {
        val reporte = dao.porId(id) ?: return ResultadoCancelacion.ELIMINADO

        if (reporte.estadoEnvio != EstadoEnvio.ENVIADO) {
            dao.borrar(id)
            almacenFotos.borrar(reporte.fotoRuta)
            return ResultadoCancelacion.ELIMINADO
        }

        val remoteId = reporte.remoteId ?: return ResultadoCancelacion.NO_PERMITIDO
        val actual = remoto.obtener(remoteId).getOrElse { return ResultadoCancelacion.SIN_CONEXION }
        if (!actual.estado.vecinoPuedeCancelar) {
            dao.actualizarEstado(id, actual.estado, actual.motivoRechazo, actual.fotoArregloUrl)
            return ResultadoCancelacion.NO_PERMITIDO
        }

        return remoto.actualizar(actual.copy(estado = EstadoReporte.CANCELADO)).fold(
            onSuccess = { guardado ->
                dao.actualizarEstado(id, guardado.estado, guardado.motivoRechazo, guardado.fotoArregloUrl)
                ResultadoCancelacion.CANCELADO
            },
            onFailure = { ResultadoCancelacion.SIN_CONEXION }
        )
    }

    private suspend fun actualizarDesdeServidor(reporte: ReporteEntity) {
        val remoteId = reporte.remoteId ?: return
        remoto.seguimiento(remoteId).onSuccess { s ->
            dao.actualizarEstado(reporte.id, s.estado, s.motivoRechazo, s.fotoArregloUrl)
        }
    }

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
