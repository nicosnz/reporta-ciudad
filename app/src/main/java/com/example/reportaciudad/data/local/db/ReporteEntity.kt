package com.example.reportaciudad.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reportaciudad.data.model.EstadoEnvio
import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.MotivoRechazo
import com.example.reportaciudad.data.model.TipoEmergencia
import com.example.reportaciudad.data.model.TipoProblema

@Entity(tableName = "reportes")
data class ReporteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val remoteId: String? = null,
    val emergencia: TipoEmergencia?,
    val problema: TipoProblema?,
    val latitud: Double,
    val longitud: Double,
    val fotoRuta: String?,
    val fotoUrl: String? = null,
    val fechaHora: Long,
    val estadoEnvio: EstadoEnvio,
    @ColumnInfo(defaultValue = "RECIBIDO")
    val estado: EstadoReporte = EstadoReporte.RECIBIDO,
    val motivoRechazo: MotivoRechazo? = null,
    val fotoArregloUrl: String? = null
)
