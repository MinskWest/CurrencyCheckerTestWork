package com.example.currencycheckertestwork

import android.app.Application
import com.example.currencycheckertestwork.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AppClass : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AppClass)
            modules(
                listOf(
                    useCasesModule,
                    networkModule,
                    viewModelModule,
                    repositoryModule,
                    currencySymbolModule
                )
            )
            androidLogger(Level.DEBUG)
        }

    }

}