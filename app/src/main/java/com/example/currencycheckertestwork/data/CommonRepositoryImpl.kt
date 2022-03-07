package com.example.currencycheckertestwork.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
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

    override fun getFullDataFromRoom(): LiveData<List<Currency>> =
        Transformations.map(
            appDatabase.currencyDao().getSavedCurrencyList()
        ) { it?.savedValue ?: emptyList() }

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

    override fun getAllFavourite(): LiveData<List<Currency>> =
        Transformations.map(
            appDatabase.favouriteCurrencyDao().getFavouriteCurrencyList()
        ) { favouriteList ->
            val finalList = mutableListOf<Currency>()
            for (i in favouriteList) {
                finalList.add(FavouriteCurrency(i.name, i.value).transformToCurrency())
            }
            finalList
        }
}