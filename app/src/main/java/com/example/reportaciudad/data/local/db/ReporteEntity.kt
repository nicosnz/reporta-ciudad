package com.example.reportaciudad.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reportaciudad.data.model.EstadoEnvio
import com.example.reportaciudad.data.model.TipoEmergencia
import com.example.reportaciudad.data.model.TipoProblema

// Tabla "reportes". Los enums se guardan como texto (su nombre); Room los convierte solo.
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
    val fechaHora: Long,
    val estadoEnvio: EstadoEnvio
)
