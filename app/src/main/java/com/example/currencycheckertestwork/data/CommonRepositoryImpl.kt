package com.example.currencycheckertestwork.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.currencycheckertestwork.data.api.ApiRetrofitService
import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import com.example.currencycheckertestwork.data.storage.AppDatabase
import com.example.currencycheckertestwork.di.ApplicationScope
import com.example.currencycheckertestwork.domain.CommonRepository
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.domain.FavouriteCurrency
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.completable.CompletableFromAction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ApplicationScope
class CommonRepositoryImpl @Inject constructor(
    private val apiRetrofitService: ApiRetrofitService,
    private val appDatabase: AppDatabase
) : CommonRepository {

    override fun loadDataByRetrofit(): Single<CurrentCurrencyDTO> =
        apiRetrofitService.getAllCurrencyList()
            .onErrorReturnItem(CurrentCurrencyDTO(mapOf()))
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())

    override fun saveDataInRoom(dbCurrentCurrency: DbCurrentCurrency): Completable =
        CompletableFromAction {
            appDatabase.currencyDao()
                .insertCurrencyList(dbCurrentCurrency)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }

    override fun getFullDataFromRoom(): LiveData<List<Currency>> =
        Transformations.map(
            appDatabase.currencyDao().getSavedCurrencyList()
        ) { it?.savedValue ?: emptyList() }

    override fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency): Completable =
        Completable.fromAction {
            appDatabase.favouriteCurrencyDao()
                .insertFavouriteCurrency(dbFavouriteCurrency)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }

    override fun deleteFavourite(name: String): Completable = Completable.fromAction {
        appDatabase.favouriteCurrencyDao()
            .deleteFavouriteCurrency(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    override fun getAllFavourite(): LiveData<List<FavouriteCurrency>> =
        Transformations.map(
            appDatabase.favouriteCurrencyDao().getFavouriteCurrencyList()
        ) { favouriteList ->
            val finalList = mutableListOf<FavouriteCurrency>()
            for (i in favouriteList) {
                finalList.add(FavouriteCurrency(i.name))
            }
            finalList
        }
}