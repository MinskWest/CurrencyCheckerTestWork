package com.example.currencycheckertestwork.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.currencycheckertestwork.constants.DataMode
import com.example.currencycheckertestwork.data.asDomainModel
import com.example.currencycheckertestwork.data.transformToDbModel
import com.example.currencycheckertestwork.data.transformToFavouriteCurrency
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.domain.interaction.GetCurrencyDataUseCase
import com.example.currencycheckertestwork.domain.interaction.RoomUseCase
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.inject.Inject

class SharedViewModel @Inject constructor(
    private val getCurrencyDataUseCase: GetCurrencyDataUseCase,
    private val roomUseCase: RoomUseCase
) : ViewModel() {

    val favouriteListToView: PublishSubject<List<Currency>> = PublishSubject.create()
    val sortedListToView: PublishSubject<List<Currency>> = PublishSubject.create()
    val errorListener: PublishSubject<Boolean> = PublishSubject.create()

    private val currentCurrencyList = mutableListOf<Currency>()
    private val favouriteCurrencyList = mutableListOf<Currency>()

    val currentCurrency: Observable<Unit> = roomUseCase.getFullList()
        .map {
            updateList(currentCurrencyList, it)
            sortedListToView.onNext(it)
        }
    val favouriteCurrency: Observable<Unit> = roomUseCase.getAllFavourite()
        .map {
            updateList(favouriteCurrencyList, it)
            favouriteListToView.onNext(it)
        }

    private val commonExecutor = Executors.newSingleThreadExecutor()
    private val cycledExecutor = Executors.newSingleThreadScheduledExecutor()

    fun loadData() {

        //load data from server
        commonExecutor.submit {
            try {
                val response = getCurrencyDataUseCase.loadData().execute()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val list = response.body()!!.asDomainModel().value
                        sortedListToView.onNext(list)
                        roomUseCase.saveInRoom(
                            response.body()!!.asDomainModel().transformToDbModel()
                        )
                    }
                }
            } catch (e: IOException) {
                errorListener.onNext(true)
            }
        }

//        for cycled updating favourites use -
//        cycledExecutor.scheduleAtFixedRate(
//            Runnable {
//                //cycle favourite update here
//                //for send to main thread - handler / runOnUiThread{}
//            }, 0, 0.1.toLong(), TimeUnit.SECONDS
//        )

    }

    private fun updateList(list: MutableList<Currency>, newValue: List<Currency>) =
        with(list) {
            clear()
            addAll(newValue)
        }


    fun finalListToView(sortedMode: DataMode, isFavourite: Boolean) =
        when (isFavourite) {
            true -> favouriteListToView.onNext(findOutFinalList(sortedMode, favouriteCurrencyList))
            false -> sortedListToView.onNext(findOutFinalList(sortedMode, currentCurrencyList))
        }

    private fun findOutFinalList(
        sortedMode: DataMode,
        currentList: MutableList<Currency>
    ): List<Currency> =
        when (sortedMode) {
            DataMode.MODE_DEFAULT -> currentList
            DataMode.MODE_SORTED_BY_VALUE -> currentList.sortedBy { it.value }
            DataMode.MODE_SORTED_BY_VALUE_VV -> currentList.sortedByDescending { it.value }
            DataMode.MODE_SORTED_BY_NAME -> currentList.sortedBy { it.name }
            DataMode.MODE_SORTED_BY_NAME_VV -> currentList.sortedByDescending { it.name }
        }

    fun insertFavourite(currency: Currency): Future<*> = commonExecutor.submit {
        roomUseCase.saveFavourite(currency.transformToFavouriteCurrency().transformToDbModel())
    }

    fun deleteFavourite(name: String): Future<*> =
        commonExecutor.submit { roomUseCase.deleteFavourite(name) }

    fun setSearch(text: String, isFavouriteMode: Boolean) =
        when (isFavouriteMode) {
            true -> {
                val finalList = favouriteCurrencyList.filter {
                    it.name.uppercase(Locale.getDefault())
                        .contains(text.uppercase(Locale.getDefault()))
                }
                favouriteListToView.onNext(finalList)
            }
            false -> {
                val finalList = currentCurrencyList.filter {
                    it.name.uppercase(Locale.getDefault())
                        .contains(text.uppercase(Locale.getDefault()))
                }
                sortedListToView.onNext(finalList)
            }
        }
}