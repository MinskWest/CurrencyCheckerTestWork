package com.example.currencycheckertestwork.domain

import com.example.currencycheckertestwork.data.CurrentCurrencyDTO
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import kotlinx.coroutines.flow.Flow

interface CommonRepository {

    suspend fun loadDataByRetrofit(): Flow<Result<CurrentCurrencyDTO>>

    suspend fun getFullDataFromRoom(): Flow<List<Currency>>

    suspend fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency)

    suspend fun deleteFavourite(name: String)

    suspend fun getAllFavourite(): Flow<List<Currency>>

}