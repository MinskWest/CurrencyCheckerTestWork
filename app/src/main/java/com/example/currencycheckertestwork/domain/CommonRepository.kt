package com.example.currencycheckertestwork.domain

import androidx.lifecycle.LiveData
import com.example.currencycheckertestwork.data.CurrentCurrencyDTO
import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import io.reactivex.Completable
import io.reactivex.Single

interface CommonRepository {

    fun loadDataByRetrofit(): Single<CurrentCurrencyDTO>

    fun saveDataInRoom(dbCurrentCurrency: DbCurrentCurrency): Completable

    fun getFullDataFromRoom(): LiveData<List<Currency>>

    fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency): Completable

    fun deleteFavourite(name: String): Completable

    fun getAllFavourite(): LiveData<List<FavouriteCurrency>>

}