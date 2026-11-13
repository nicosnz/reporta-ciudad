package com.example.reportaciudad.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ReporteEntity::class], version = 1, exportSchema = true)
abstract class ReportaCiudadDatabase : RoomDatabase() {

    abstract fun reporteDao(): ReporteDao

    companion object {
        private const val NOMBRE = "reporta_ciudad.db"

        fun crear(context: Context): ReportaCiudadDatabase =
            Room.databaseBuilder(context.applicationContext, ReportaCiudadDatabase::class.java, NOMBRE)
                .build()
    }
}
