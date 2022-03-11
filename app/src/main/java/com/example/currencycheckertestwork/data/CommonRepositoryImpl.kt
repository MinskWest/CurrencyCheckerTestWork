package com.example.currencycheckertestwork.data

import com.example.currencycheckertestwork.data.api.ApiRetrofitService
import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import com.example.currencycheckertestwork.data.storage.AppDatabase
import com.example.currencycheckertestwork.di.ApplicationScope
import com.example.currencycheckertestwork.domain.CommonRepository
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.domain.FavouriteCurrency
import retrofit2.Call
import javax.inject.Inject

@ApplicationScope
class CommonRepositoryImpl @Inject constructor(
    private val apiRetrofitService: ApiRetrofitService,
    private val appDatabase: AppDatabase
) : CommonRepository {

    override fun loadDataByRetrofit(): Call<CurrentCurrencyDTO> =
        apiRetrofitService.getAllCurrencyList()

    override fun saveDataInRoom(dbCurrentCurrency: DbCurrentCurrency) =
        appDatabase.currencyDao().insertCurrencyList(dbCurrentCurrency)

    override fun getFullDataFromRoom(): List<Currency> =
        appDatabase.currencyDao().getSavedCurrencyList().savedValue

    override fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency) =
        appDatabase.favouriteCurrencyDao().insertFavouriteCurrency(dbFavouriteCurrency)

    override fun deleteFavourite(name: String) =
        appDatabase.favouriteCurrencyDao().deleteFavouriteCurrency(name)

    override fun getAllFavourite(): List<Currency> {
        val list = appDatabase.favouriteCurrencyDao().getFavouriteCurrencyList()
        val finalList = mutableListOf<Currency>()
        for (i in list) {
            finalList.add(FavouriteCurrency(i.name, i.value).transformToCurrency())
        }
        return finalList.toList()
    }

}