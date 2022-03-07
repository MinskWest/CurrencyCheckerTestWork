package com.example.currencycheckertestwork.util

import android.view.View
import android.widget.TextView

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.setVisible(visible: Boolean) = if (visible) show() else hide()

fun View.onClick(action: () -> Unit) = setOnClickListener { action() }

fun View.findTV(id: Int) = findViewById<TextView>(id)