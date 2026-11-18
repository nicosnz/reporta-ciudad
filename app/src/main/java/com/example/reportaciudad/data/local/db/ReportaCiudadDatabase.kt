package com.example.reportaciudad.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ReporteEntity::class], version = 2, exportSchema = true)
abstract class ReportaCiudadDatabase : RoomDatabase() {

    abstract fun reporteDao(): ReporteDao

    companion object {
        private const val NOMBRE = "reporta_ciudad.db"

        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE reportes ADD COLUMN fotoUrl TEXT")
                db.execSQL("ALTER TABLE reportes ADD COLUMN estado TEXT NOT NULL DEFAULT 'RECIBIDO'")
                db.execSQL("ALTER TABLE reportes ADD COLUMN motivoRechazo TEXT")
                db.execSQL("ALTER TABLE reportes ADD COLUMN fotoArregloUrl TEXT")
            }
        }

        fun crear(context: Context): ReportaCiudadDatabase =
            Room.databaseBuilder(context.applicationContext, ReportaCiudadDatabase::class.java, NOMBRE)
                .addMigrations(MIGRACION_1_2)
                .build()
    }
}
