package com.example.reportaciudad.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.reportaciudad.data.model.EstadoEnvio
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteDao {

    // Flow: la UI se actualiza sola cada vez que cambia la tabla
    @Query("SELECT * FROM reportes ORDER BY fechaHora DESC")
    fun observarTodos(): Flow<List<ReporteEntity>>

    @Query("SELECT * FROM reportes WHERE id = :id")
    fun observarPorId(id: Long): Flow<ReporteEntity?>

    @Query("SELECT * FROM reportes WHERE estadoEnvio != 'ENVIADO'")
    suspend fun pendientes(): List<ReporteEntity>

    @Insert
    suspend fun insertar(reporte: ReporteEntity): Long

    @Query("UPDATE reportes SET estadoEnvio = :estado, remoteId = :remoteId WHERE id = :id")
    suspend fun actualizarEnvio(id: Long, estado: EstadoEnvio, remoteId: String?)
}
