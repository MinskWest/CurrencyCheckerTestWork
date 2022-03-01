package com.example.currencycheckertestwork

import android.app.Application
import com.example.currencycheckertestwork.di.DaggerMainComponent
import com.example.currencycheckertestwork.di.MainComponent

class AppClass: Application() {

    lateinit var mainComponent: MainComponent

    override fun onCreate() {
        super.onCreate()
        mainComponent = DaggerMainComponent.factory().create(this)
    }
}