package com.example.currencycheckertestwork.presentation.activities

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.currencycheckertestwork.AppClass
import com.example.currencycheckertestwork.R
import com.example.currencycheckertestwork.di.MainComponent

class MainActivity : AppCompatActivity() {

    private val component: MainComponent by lazy {
        (application as AppClass).mainComponent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializing()
    }

    private fun initializing() {
        component.inject(this)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

}