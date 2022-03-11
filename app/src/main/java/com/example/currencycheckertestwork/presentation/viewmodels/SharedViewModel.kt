package com.example.currencycheckertestwork.presentation.viewmodels

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currencycheckertestwork.constants.DataMode
import com.example.currencycheckertestwork.data.asDomainModel
import com.example.currencycheckertestwork.data.transformToDbModel
import com.example.currencycheckertestwork.data.transformToFavouriteCurrency
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.domain.interaction.GetCurrencyDataUseCase
import com.example.currencycheckertestwork.domain.interaction.RoomUseCase
import java.io.IOException
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.inject.Inject

class SharedViewModel @Inject constructor(
    private val getCurrencyDataUseCase: GetCurrencyDataUseCase,
    private val roomUseCase: RoomUseCase
) : ViewModel() {

    val favouriteListToView = MutableLiveData<List<Currency>>()
    val sortedListToView = MutableLiveData<List<Currency>>()
    val errorListener = MutableLiveData(false)

    private val currentCurrencyList = CopyOnWriteArrayList<Currency>()
    private val favouriteCurrencyList = CopyOnWriteArrayList<Currency>()

    private val commonExecutor by lazy { Executors.newSingleThreadExecutor() }

    fun loadData() {

        //load data from server
        commonExecutor.submit {
            try {
                val response = getCurrencyDataUseCase.loadData().execute()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val list = response.body()!!.asDomainModel().value
                        runOnUi { sortedListToView.value = list }
                        roomUseCase.saveInRoom(
                            response.body()!!.asDomainModel().transformToDbModel()
                        )
                    }
                }
            } catch (e: IOException) {
                errorListener.postValue(true)
            }

            try {
                updateList(currentCurrencyList, roomUseCase.getFullList())
                updateList(favouriteCurrencyList, roomUseCase.getAllFavourite())
            } catch (e: IOException) {
                errorListener.postValue(true)
            }

            runOnUi {
                sortedListToView.value = currentCurrencyList
                favouriteListToView.value = favouriteCurrencyList
            }

        }

    }

    private fun runOnUi(runnable: Runnable) = Handler(Looper.getMainLooper()).post(runnable)

    private fun updateList(list: MutableList<Currency>, newValue: List<Currency>) =
        with(list) {
            clear()
            addAll(newValue)
        }


    fun finalListToView(sortedMode: DataMode, isFavourite: Boolean) =
        when (isFavourite) {
            true -> favouriteListToView.postValue(
                findOutFinalList(
                    sortedMode,
                    favouriteCurrencyList
                )
            )
            false -> sortedListToView.postValue(findOutFinalList(sortedMode, currentCurrencyList))
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
                favouriteListToView.postValue(finalList)
            }
            false -> {
                val finalList = currentCurrencyList.filter {
                    it.name.uppercase(Locale.getDefault())
                        .contains(text.uppercase(Locale.getDefault()))
                }
                sortedListToView.postValue(finalList)
            }
        }
}