package com.example.currencycheckertestwork.domain.interaction

import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import com.example.currencycheckertestwork.domain.CommonRepository
import com.example.currencycheckertestwork.domain.Currency
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class RoomUseCase @Inject constructor(
    private val commonRepository: CommonRepository
) {

    fun saveInRoom(dbCurrentCurrency: DbCurrentCurrency): Completable =
        commonRepository.saveDataInRoom(dbCurrentCurrency)

    fun getFullList(): Observable<List<Currency>> =
        commonRepository.getFullDataFromRoom()

    fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency): Completable =
        commonRepository.saveFavourite(dbFavouriteCurrency)

    fun deleteFavourite(name: String): Completable =
        commonRepository.deleteFavourite(name)

    fun getAllFavourite(): Observable<List<Currency>> =
        commonRepository.getAllFavourite()
}