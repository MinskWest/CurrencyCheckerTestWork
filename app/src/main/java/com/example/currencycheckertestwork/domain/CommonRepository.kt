package com.example.currencycheckertestwork.domain

import com.example.currencycheckertestwork.data.CurrentCurrencyDTO
import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import retrofit2.Call

interface CommonRepository {

    fun loadDataByRetrofit(): Call<CurrentCurrencyDTO>

    fun saveDataInRoom(dbCurrentCurrency: DbCurrentCurrency)

    fun getFullDataFromRoom(): List<Currency>

    fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency)

    fun deleteFavourite(name: String)

    fun getAllFavourite(): List<Currency>

}