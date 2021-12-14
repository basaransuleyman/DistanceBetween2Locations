package com.example.maps

import android.app.Application
import com.example.maps.di.KoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        insertKoin()
    }

    private fun insertKoin() {
        startKoin {
            androidContext(this@Application)
            modules(listOf(KoinModule.viewModelModule))
        }
    }
}