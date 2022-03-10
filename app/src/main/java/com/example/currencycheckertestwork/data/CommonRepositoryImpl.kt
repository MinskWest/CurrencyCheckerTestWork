package com.example.currencycheckertestwork.data

import com.example.currencycheckertestwork.data.api.ApiRetrofitService
import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import com.example.currencycheckertestwork.data.storage.AppDatabase
import com.example.currencycheckertestwork.domain.CommonRepository
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.domain.FavouriteCurrency
import com.example.currencycheckertestwork.domain.Result
import com.example.currencycheckertestwork.util.api.ErrorUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.Response
import retrofit2.Retrofit

class CommonRepositoryImpl(
    private val apiRetrofitService: ApiRetrofitService,
    private val appDatabase: AppDatabase
) : CommonRepository {

    override suspend fun loadDataByRetrofit(): Flow<Result<CurrentCurrencyDTO>> =
        flow {
            //set loading status
            emit(Result.loading())
            //get response
            val result = loadCurrencyApiAction()
            if (result.status == Result.Status.SUCCESS) {
                result.data?.asDomainModel()?.let { it ->
                    //Cache in room if successful
                    saveDataInRoom(it.transformToDbModel())
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)

    private suspend fun loadCurrencyApiAction(): Result<CurrentCurrencyDTO> =
        getResponse(
            request = { apiRetrofitService.getAllCurrencyList() },
            defaultErrorMessage = "Error load currency data from server"
        )

    private suspend fun saveDataInRoom(dbCurrentCurrency: DbCurrentCurrency) =
        appDatabase.currencyDao().insertCurrencyList(dbCurrentCurrency)

    override suspend fun getFullDataFromRoom(): Flow<List<Currency>> =
        appDatabase.currencyDao()
            .getSavedCurrencyList()
            .map {
                it?.savedValue ?: listOf()
            }.flowOn(Dispatchers.IO)

    override suspend fun saveFavourite(dbFavouriteCurrency: DbFavouriteCurrency): Unit =
        appDatabase.favouriteCurrencyDao().insertFavouriteCurrency(dbFavouriteCurrency)

    override suspend fun deleteFavourite(name: String) =
        appDatabase.favouriteCurrencyDao().deleteFavouriteCurrency(name)

    override suspend fun getAllFavourite(): Flow<List<Currency>> =
        appDatabase.favouriteCurrencyDao()
            .getFavouriteCurrencyList()
            .map { favouriteList ->
                val finalList = mutableListOf<Currency>()
                for (i in favouriteList ?: listOf()) {
                    finalList.add(FavouriteCurrency(i.name, i.value).transformToCurrency())
                }
                finalList
            }.flowOn(Dispatchers.IO)

    private suspend fun <T> getResponse(
        request: suspend () -> Response<T>,
        defaultErrorMessage: String
    ): Result<T> {
        return try {
            val result = request.invoke()
            if (result.isSuccessful) {
                return Result.success(result.body())
            } else {
                val errorResponse = ErrorUtils.parseError(result, Retrofit.Builder().build())
                Result.error(errorResponse?.status_message ?: defaultErrorMessage, errorResponse)
            }
        } catch (e: Throwable) {
            Result.error("Unknown Error", null)
        }
    }
}