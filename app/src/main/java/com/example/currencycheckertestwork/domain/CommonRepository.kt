package com.example.currencycheckertestwork.domain

import com.example.currencycheckertestwork.data.CurrentCurrencyDTO
import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import io.reactivex.Observable
import retrofit2.Call

interface CommonRepository {

    fun loadDataByRetrofit(): Call<CurrentCurrencyDTO>

    fun saveDataInRoom(dbCurrentCurrency: DbCurrentCurrency)

    fun getFullDataFromRoom(): Observable<List<Currency>>

    fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency)

    fun deleteFavourite(name: String)

    fun getAllFavourite(): Observable<List<Currency>>

}