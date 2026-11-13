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
import com.example.reportaciudad.data.remote.ReporteRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class ReporteRepositoryImpl(
    private val dao: ReporteDao,
    private val remoto: ReporteRemoteDataSource,
    private val almacenFotos: AlmacenFotos,
    private val ubicacionActual: UbicacionActual,
    private val ahora: () -> Long = System::currentTimeMillis
) : ReporteRepository {

    // Evita mandar el mismo reporte dos veces si se cruzan un envío y un reintento
    private val sincronizando = Mutex()

    override val reportes: Flow<List<Reporte>> =
        dao.observarTodos().map { lista -> lista.map { it.aDominio() } }

    override fun reporte(id: Long): Flow<Reporte?> =
        dao.observarPorId(id).map { it?.aDominio() }

    override suspend fun enviar(formulario: ReporteFormulario): EstadoEnvio {
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
        return sincronizando.withLock { sincronizar(nuevo.copy(id = id)) }
    }

    override suspend fun reintentarPendientes() {
        if (!sincronizando.tryLock()) return     // ya hay una sincronización en curso
        try {
            dao.pendientes().forEach { sincronizar(it) }
        } finally {
            sincronizando.unlock()
        }
    }

    private suspend fun sincronizar(reporte: ReporteEntity): EstadoEnvio =
        remoto.enviar(reporte.aDominio()).fold(
            onSuccess = { remoteId ->
                dao.actualizarEnvio(reporte.id, EstadoEnvio.ENVIADO, remoteId)
                EstadoEnvio.ENVIADO
            },
            onFailure = {
                dao.actualizarEnvio(reporte.id, EstadoEnvio.ERROR, null)
                EstadoEnvio.ERROR
            }
        )

    override fun nuevoArchivoFoto(): File = almacenFotos.crearArchivo()

    override fun descartarFoto(ruta: String?) = almacenFotos.borrar(ruta)

    override suspend fun obtenerUbicacion(): Ubicacion? = ubicacionActual.obtener()
}
