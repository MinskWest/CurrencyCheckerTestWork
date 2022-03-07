package com.example.currencycheckertestwork.data

import com.example.currencycheckertestwork.constants.RETROFIT_LOAD_ERROR
import com.example.currencycheckertestwork.data.api.ApiRetrofitService
import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import com.example.currencycheckertestwork.data.storage.AppDatabase
import com.example.currencycheckertestwork.di.ApplicationScope
import com.example.currencycheckertestwork.domain.CommonRepository
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.domain.FavouriteCurrency
import com.example.currencycheckertestwork.domain.scheduler.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

@ApplicationScope
class CommonRepositoryImpl @Inject constructor(
    private val apiRetrofitService: ApiRetrofitService,
    private val schedulerProvider: SchedulerProvider,
    private val appDatabase: AppDatabase
) : CommonRepository {

    override fun loadDataByRetrofit(): Single<CurrentCurrencyDTO> =
        apiRetrofitService.getAllCurrencyList()
            .onErrorReturnItem(CurrentCurrencyDTO(mapOf(Pair(RETROFIT_LOAD_ERROR, 0.0))))
            .observeOn(schedulerProvider.io())
            .subscribeOn(schedulerProvider.io())

    override fun saveDataInRoom(dbCurrentCurrency: DbCurrentCurrency): Completable =
        appDatabase.currencyDao()
            .insertCurrencyList(dbCurrentCurrency)
            .observeOn(schedulerProvider.io())
            .subscribeOn(schedulerProvider.io())

    override fun getFullDataFromRoom(): Observable<List<Currency>> =
        appDatabase.currencyDao()
            .getSavedCurrencyList()
            .observeOn(schedulerProvider.io())
            .subscribeOn(schedulerProvider.io())
            .map { it.savedValue }
            .toObservable()

    override fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency): Completable =
        appDatabase.favouriteCurrencyDao()
            .insertFavouriteCurrency(dbFavouriteCurrency)
            .observeOn(schedulerProvider.io())
            .subscribeOn(schedulerProvider.io())

    override fun deleteFavourite(name: String): Completable =
        appDatabase.favouriteCurrencyDao()
            .deleteFavouriteCurrency(name)
            .observeOn(schedulerProvider.io())
            .subscribeOn(schedulerProvider.io())

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