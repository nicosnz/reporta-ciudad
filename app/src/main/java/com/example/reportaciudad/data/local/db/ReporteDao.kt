package com.example.reportaciudad.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.reportaciudad.data.model.EstadoEnvio
import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.MotivoRechazo
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteDao {


    @Query("SELECT * FROM reportes ORDER BY fechaHora DESC")
    fun observarTodos(): Flow<List<ReporteEntity>>

    @Query("SELECT * FROM reportes WHERE id = :id")
    fun observarPorId(id: Long): Flow<ReporteEntity?>

    @Query("SELECT * FROM reportes WHERE id = :id")
    suspend fun porId(id: Long): ReporteEntity?

    @Query("SELECT * FROM reportes WHERE estadoEnvio != 'ENVIADO'")
    suspend fun pendientes(): List<ReporteEntity>

    // Enviados cuyo estado todavía puede cambiar (resuelto y rechazado son finales)
    @Query(
        "SELECT * FROM reportes WHERE estadoEnvio = 'ENVIADO' AND remoteId IS NOT NULL " +
            "AND estado NOT IN ('RESUELTO', 'RECHAZADO')"
    )
    suspend fun enviadosAbiertos(): List<ReporteEntity>

    @Query("SELECT estadoEnvio FROM reportes WHERE id = :id")
    suspend fun estadoEnvio(id: Long): EstadoEnvio?

    @Insert
    suspend fun insertar(reporte: ReporteEntity): Long

    @Query("UPDATE reportes SET estadoEnvio = :estado, remoteId = :remoteId WHERE id = :id")
    suspend fun actualizarEnvio(id: Long, estado: EstadoEnvio, remoteId: String?)

    @Query("UPDATE reportes SET fotoUrl = :fotoUrl WHERE id = :id")
    suspend fun actualizarFotoUrl(id: Long, fotoUrl: String)

    @Query(
        "UPDATE reportes SET estado = :estado, motivoRechazo = :motivoRechazo, " +
            "fotoArregloUrl = :fotoArregloUrl WHERE id = :id"
    )
    suspend fun actualizarEstado(
        id: Long,
        estado: EstadoReporte,
        motivoRechazo: MotivoRechazo?,
        fotoArregloUrl: String?
    )
}
