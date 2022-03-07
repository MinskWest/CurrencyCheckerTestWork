package com.example.currencycheckertestwork.data

import com.example.currencycheckertestwork.data.api.ApiRetrofitService
import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import com.example.currencycheckertestwork.data.storage.AppDatabase
import com.example.currencycheckertestwork.di.ApplicationScope
import com.example.currencycheckertestwork.domain.CommonRepository
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.domain.FavouriteCurrency
import com.example.currencycheckertestwork.domain.scheduler.SchedulerProvider
import io.reactivex.Observable
import retrofit2.Call
import javax.inject.Inject

@ApplicationScope
class CommonRepositoryImpl @Inject constructor(
    private val apiRetrofitService: ApiRetrofitService,
    private val schedulerProvider: SchedulerProvider,
    private val appDatabase: AppDatabase
) : CommonRepository {

    override fun loadDataByRetrofit(): Call<CurrentCurrencyDTO> =
        apiRetrofitService.getAllCurrencyList()

    override fun saveDataInRoom(dbCurrentCurrency: DbCurrentCurrency) =
        appDatabase.currencyDao().insertCurrencyList(dbCurrentCurrency)

    override fun getFullDataFromRoom(): Observable<List<Currency>> =
        appDatabase.currencyDao()
            .getSavedCurrencyList()
            .observeOn(schedulerProvider.io())
            .subscribeOn(schedulerProvider.io())
            .map { it.savedValue }
            .toObservable()

    override fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency) =
        appDatabase.favouriteCurrencyDao().insertFavouriteCurrency(dbFavouriteCurrency)

    override fun deleteFavourite(name: String) =
        appDatabase.favouriteCurrencyDao().deleteFavouriteCurrency(name)

    override fun getAllFavourite(): Observable<List<Currency>> =
        appDatabase.favouriteCurrencyDao()
            .getFavouriteCurrencyList()
            .observeOn(schedulerProvider.io())
            .subscribeOn(schedulerProvider.io())
            .map {
                val finalList = mutableListOf<Currency>()
                for (i in it) {
                    finalList.add(FavouriteCurrency(i.name, i.value).transformToCurrency())
                }
                finalList.toList()
            }
            .toObservable()
}