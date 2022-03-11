package com.example.currencycheckertestwork.constants

enum class DataMode(val IntKey: Int) {
    MODE_DEFAULT(0),
    MODE_SORTED_BY_VALUE(1),
    MODE_SORTED_BY_VALUE_VV(2),
    MODE_SORTED_BY_NAME(3),
    MODE_SORTED_BY_NAME_VV(4),
}

const val ERROR_FROM_SERVER = "Error load currency data from server"
const val UNKNOWN_ERROR = "Unknown Error"
const val HANDLER_DELAY = 1000L
const val TIMEOUT = 30 * 1000L
const val DATABASE_NAME = "currency_database"
const val MAIN_LAY_MANAGER = "main_linear_layout_manager"
const val ACCESS_KEY = "access_key"
const val FORMAT = "format"
const val DB_CURRENT_CURRENCY = "saved_current_currency_list"
const val DB_FAVOURITES = "favourite_items"
const val OUR_CURRENCY_LIST = "ourCurrencyList"
const val FAV_CURRENCY_NAME = "favCurrencyName"
const val FAV_CURRENCY_VALUE = "favCurrencyValue"