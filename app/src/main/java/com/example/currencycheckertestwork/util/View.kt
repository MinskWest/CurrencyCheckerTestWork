package com.example.currencycheckertestwork.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.setVisible(visible: Boolean) = if (visible) show() else hide()

fun View.onClick(action: () -> Unit) = setOnClickListener { action() }

fun Context.string(id: Int) = this.resources.getString(id)