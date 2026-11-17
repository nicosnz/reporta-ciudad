package com.example.reportaciudad

import android.app.Application

class ReportaCiudadApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
