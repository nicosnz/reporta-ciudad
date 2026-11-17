package com.example.reportaciudad.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.reportaciudad.data.model.EstadoEnvio
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteDao {


    @Query("SELECT * FROM reportes ORDER BY fechaHora DESC")
    fun observarTodos(): Flow<List<ReporteEntity>>

    @Query("SELECT * FROM reportes WHERE id = :id")
    fun observarPorId(id: Long): Flow<ReporteEntity?>

    @Query("SELECT * FROM reportes WHERE estadoEnvio != 'ENVIADO'")
    suspend fun pendientes(): List<ReporteEntity>

    @Query("SELECT estadoEnvio FROM reportes WHERE id = :id")
    suspend fun estadoEnvio(id: Long): EstadoEnvio?

    @Insert
    suspend fun insertar(reporte: ReporteEntity): Long

    @Query("UPDATE reportes SET estadoEnvio = :estado, remoteId = :remoteId WHERE id = :id")
    suspend fun actualizarEnvio(id: Long, estado: EstadoEnvio, remoteId: String?)
}
