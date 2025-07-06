package com.asparrin.carlos.estiloya

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class EstiloyaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar ThreeTenABP para soporte de LocalDate en Android
        AndroidThreeTen.init(this)
    }
} 