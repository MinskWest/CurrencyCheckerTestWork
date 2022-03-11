package com.example.currencycheckertestwork.util

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.currencycheckertestwork.R

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.setVisible(visible: Boolean) = if (visible) show() else hide()

fun View.onClick(action: () -> Unit) = setOnClickListener { action() }

fun Context.string(id: Int) = this.resources.getString(id)