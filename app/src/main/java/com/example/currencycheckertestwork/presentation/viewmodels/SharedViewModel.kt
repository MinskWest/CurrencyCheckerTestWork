package com.example.currencycheckertestwork.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currencycheckertestwork.constants.DataMode
import com.example.currencycheckertestwork.constants.RETROFIT_LOAD_ERROR
import com.example.currencycheckertestwork.data.CurrentCurrencyDTO
import com.example.currencycheckertestwork.data.asDomainModel
import com.example.currencycheckertestwork.data.transformToDbModel
import com.example.currencycheckertestwork.data.transformToFavouriteCurrency
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.domain.interaction.GetCurrencyDataUseCase
import com.example.currencycheckertestwork.domain.interaction.RoomUseCase
import io.reactivex.Completable
import java.util.*
import javax.inject.Inject

class SharedViewModel @Inject constructor(
    private val getCurrencyDataUseCase: GetCurrencyDataUseCase,
    private val roomUseCase: RoomUseCase
) : ViewModel() {

    private val _currencyListFromRoom = roomUseCase.getFullList()
    val currencyListFromRoom: LiveData<List<Currency>>
        get() = _currencyListFromRoom

    private val _favouriteCurrencyList = roomUseCase.getAllFavourite()
    val favouriteCurrencyList: LiveData<List<Currency>>
        get() = _favouriteCurrencyList

    val favouriteListToView = MutableLiveData<List<Currency>>()
    val sortedListToView = MutableLiveData<List<Currency>>()
    val errorListener = MutableLiveData(false)

    fun loadData() {
        getCurrencyDataUseCase.loadData()
            .doOnSuccess { currentCurrencyDTO ->
                loadCurrent(currentCurrencyDTO)
                loadFavourite()
            }
            .subscribe()
    }

    private fun loadCurrent(currentCurrencyDTO: CurrentCurrencyDTO) {
        if (currentCurrencyDTO.loadValue.containsKey(RETROFIT_LOAD_ERROR)) {
            val currentList = _currencyListFromRoom.value ?: listOf()
            if (currentList.isEmpty()) {
                // can't load and haven't saved value - view no internet popup
                errorListener.postValue(true)
            } else {
                // view saved version
                sortedListToView.postValue(currentList)
            }
        } else {
            //update data in room
            roomUseCase.saveInRoom(currentCurrencyDTO.asDomainModel().transformToDbModel())
                .doOnComplete {
                    sortedListToView.postValue(currentCurrencyDTO.asDomainModel().value)
                }
                .subscribe()
        }
    }

    private fun loadFavourite() = favouriteListToView.postValue(_favouriteCurrencyList.value)

    fun finalListToView(sortedMode: DataMode, isFavourite: Boolean) {
        when (isFavourite) {
            true ->
                favouriteListToView
                    .postValue(findOutFinalList(sortedMode, favouriteListToView) ?: emptyList())
            false ->
                sortedListToView
                    .postValue(findOutFinalList(sortedMode, currencyListFromRoom) ?: emptyList())
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

    fun insertFavourite(currency: Currency): Completable =
        roomUseCase.saveFavourite(currency.transformToFavouriteCurrency().transformToDbModel())

    fun deleteFavourite(name: String): Completable = roomUseCase.deleteFavourite(name)

    fun setSearch(text: String, isFavouriteMode: Boolean) =
        when (isFavouriteMode) {
            true -> {
                val finalList = favouriteCurrencyList.value?.filter {
                    it.name.uppercase(Locale.getDefault())
                        .contains(text.uppercase(Locale.getDefault()))
                }
                favouriteListToView.postValue(finalList ?: listOf())
            }
            false -> {
                val finalList = currencyListFromRoom.value?.filter {
                    it.name.uppercase(Locale.getDefault())
                        .contains(text.uppercase(Locale.getDefault()))
                }
                sortedListToView.postValue(finalList ?: listOf())
            }
        }
}