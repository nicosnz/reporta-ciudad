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
    // Id que asigna el servidor (MockAPI); null mientras el reporte no se haya enviado
    val remoteId: String? = null,
    // Uno de los dos es null: o es emergencia o es problema de rutina
    val emergencia: TipoEmergencia?,
    val problema: TipoProblema?,
    val latitud: Double,
    val longitud: Double,
    // Ruta del archivo en files/fotos/; la imagen no se guarda en la base
    val fotoRuta: String?,
    // URL en Cloudinary. Se guarda apenas se sube: un reintento no vuelve a subir la foto
    val fotoUrl: String? = null,
    val fechaHora: Long,
    val estadoEnvio: EstadoEnvio,
    // Lo que trae el servidor (versión 2 de la base)
    @ColumnInfo(defaultValue = "RECIBIDO")
    val estado: EstadoReporte = EstadoReporte.RECIBIDO,
    val motivoRechazo: MotivoRechazo? = null,
    val fotoArregloUrl: String? = null
)
