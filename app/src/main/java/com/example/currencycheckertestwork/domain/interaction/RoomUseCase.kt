package com.example.currencycheckertestwork.domain.interaction

import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import com.example.currencycheckertestwork.domain.CommonRepository
import com.example.currencycheckertestwork.domain.Currency
import javax.inject.Inject

class RoomUseCase @Inject constructor(
    private val commonRepository: CommonRepository
) {

    fun saveInRoom(dbCurrentCurrency: DbCurrentCurrency) =
        commonRepository.saveDataInRoom(dbCurrentCurrency)

    fun getFullList(): List<Currency> = commonRepository.getFullDataFromRoom()

    fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency) =
        commonRepository.saveFavourite(dbFavouriteCurrency)

    fun deleteFavourite(name: String) =
        commonRepository.deleteFavourite(name)

    fun getAllFavourite(): List<Currency> =
        commonRepository.getAllFavourite()
}