package com.example.currencycheckertestwork.presentation.activities

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.currencycheckertestwork.AppClass
import com.example.currencycheckertestwork.R
import com.example.currencycheckertestwork.di.MainComponent
import com.example.currencycheckertestwork.util.setVisible
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.error_view.*

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

    fun showBasePopup(message: String){
        errorView.setVisible(true)
        errorText.text = message
    }

}