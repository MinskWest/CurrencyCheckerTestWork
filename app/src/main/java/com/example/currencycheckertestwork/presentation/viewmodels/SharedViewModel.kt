package com.example.currencycheckertestwork.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currencycheckertestwork.constants.DataMode
import com.example.currencycheckertestwork.data.asDomainModel
import com.example.currencycheckertestwork.data.transformToDbModel
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.domain.FavouriteCurrency
import com.example.currencycheckertestwork.domain.interaction.GetCurrencyDataUseCase
import com.example.currencycheckertestwork.domain.interaction.RoomUseCase
import java.util.*
import javax.inject.Inject

class SharedViewModel @Inject constructor(
    private val getCurrencyDataUseCase: GetCurrencyDataUseCase,
    private val roomUseCase: RoomUseCase
) : ViewModel() {

    val currencyListFromRoom = roomUseCase.getFullList()
    val favouriteCurrencyList = roomUseCase.getAllFavourite()
    val favouriteListToView = MutableLiveData<List<Currency>>()
    val sortedListToView = MutableLiveData<List<Currency>>()

    fun loadData() {
        getCurrencyDataUseCase.loadData()
            .doOnSuccess { serverResponse ->
                if (serverResponse.loadValue.isNotEmpty()) {
                    val isFirstLoad = roomUseCase.getFullList().value == null
                    roomUseCase.saveInRoom(serverResponse.asDomainModel().transformToDbModel())
                        .doOnComplete {
                            loadFavourite()
                            if (isFirstLoad) {
                                sortedListToView.postValue(serverResponse.asDomainModel().value)
                            } else finalListToView(DataMode.MODE_DEFAULT, false)
                        }
                        .subscribe()
                }
            }
            .subscribe()
    }

    fun finalListToView(sortedMode: DataMode, isFavourite: Boolean) {
        when (isFavourite) {
            true ->
                favouriteListToView.postValue(
                    findOutFinalList(sortedMode, favouriteListToView) ?: emptyList()
                )
            false ->
                sortedListToView.postValue(
                    findOutFinalList(sortedMode, currencyListFromRoom) ?: emptyList()
                )
        }
    }

    private fun findOutFinalList(
        sortedMode: DataMode,
        liveDataType: LiveData<List<Currency>>
    ): List<Currency>? =
        when (sortedMode) {
            DataMode.MODE_DEFAULT -> liveDataType.value
            DataMode.MODE_SORTED_BY_VALUE -> liveDataType.value?.sortedBy { it.value }
            DataMode.MODE_SORTED_BY_VALUE_VV -> liveDataType.value?.sortedByDescending { it.value }
            DataMode.MODE_SORTED_BY_NAME -> liveDataType.value?.sortedBy { it.name }
            DataMode.MODE_SORTED_BY_NAME_VV -> liveDataType.value?.sortedByDescending { it.name }
        }


    fun insertFavourite(name: String) {
        if (name.isNotEmpty()) roomUseCase.saveFavourite(FavouriteCurrency(name).transformToDbModel())
            .subscribe()
    }

    fun deleteFavourite(name: String) {
        if (name.isNotEmpty()) roomUseCase.deleteFavourite(name).subscribe()
    }

    private fun loadFavourite() = favouriteListToView.postValue(getFavoriteTypes())

    fun setSearch(text: String, isFavouriteMode: Boolean) {
        when (isFavouriteMode) {
            true -> {
                val finalList = getFavoriteTypes().filter {
                    it.name.uppercase(Locale.getDefault())
                        .contains(text.uppercase(Locale.getDefault()))
                }
                favouriteListToView.postValue(finalList)
            }
            false -> {
                val finalList = currencyListFromRoom.value?.filter {
                    it.name.uppercase(Locale.getDefault())
                        .contains(text.uppercase(Locale.getDefault()))
                }
                sortedListToView.postValue(finalList ?: emptyList())
            }
        }
    }

    private fun getFavoriteTypes(): MutableList<Currency> {
        val symbols = mutableListOf<String>()
        val favouriteList = mutableListOf<Currency>()
        for (i in favouriteCurrencyList.value ?: emptyList()) {
            symbols.add(i.name)
        }
        for (i in currencyListFromRoom.value ?: emptyList()) {
            if (symbols.contains(i.name)) {
                favouriteList.add(i)
            }
        }
        return favouriteList
    }

}