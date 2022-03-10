package com.example.currencycheckertestwork.domain.interaction

import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import com.example.currencycheckertestwork.domain.CommonRepository
import com.example.currencycheckertestwork.domain.Currency
import kotlinx.coroutines.flow.Flow

class RoomUseCase(private val commonRepository: CommonRepository) {

    suspend fun getFullList(): Flow<List<Currency>> = commonRepository.getFullDataFromRoom()

    suspend fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency) =
        commonRepository.saveFavourite(dbFavouriteCurrency)

    suspend fun deleteFavourite(name: String) = commonRepository.deleteFavourite(name)

    suspend fun getAllFavourite(): Flow<List<Currency>> = commonRepository.getAllFavourite()

}