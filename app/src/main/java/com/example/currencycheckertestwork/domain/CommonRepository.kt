package com.example.currencycheckertestwork.domain

import com.example.currencycheckertestwork.data.CurrentCurrencyDTO
import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface CommonRepository {

    fun loadDataByRetrofit(): Single<CurrentCurrencyDTO>

    fun saveDataInRoom(dbCurrentCurrency: DbCurrentCurrency): Completable

    fun getFullDataFromRoom(): Observable<List<Currency>>

    fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency): Completable

    fun deleteFavourite(name: String): Completable

    fun getAllFavourite(): Observable<List<Currency>>

}