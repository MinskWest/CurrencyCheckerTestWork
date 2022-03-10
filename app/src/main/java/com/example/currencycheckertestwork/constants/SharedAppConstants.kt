package com.example.currencycheckertestwork.constants

enum class DataMode(val IntKey: Int) {
    MODE_DEFAULT(0),
    MODE_SORTED_BY_VALUE(1),
    MODE_SORTED_BY_VALUE_VV(2),
    MODE_SORTED_BY_NAME(3),
    MODE_SORTED_BY_NAME_VV(4),
}

const val RETROFIT_LOAD_ERROR = "error"
const val HANDLER_DELAY = 1000L
const val TIMEOUT = 30 * 1000L
const val DATABASE_NAME = "currency_database"
const val MAIN_LAY_MANAGER = "main_linear_layout_manager"