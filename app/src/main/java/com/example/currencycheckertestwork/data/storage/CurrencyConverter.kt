package com.example.currencycheckertestwork.data.storage

import androidx.room.TypeConverter
import com.example.currencycheckertestwork.domain.Currency
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type
import java.util.*

class CurrencyConverter {

    @TypeConverter
    fun stringToList(data: String): List<Currency> {
        val listType: Type = object : TypeToken<List<Currency>>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun currencyListToString(currency: List<Currency>): String {
        return Gson().toJson(currency)
    }
}