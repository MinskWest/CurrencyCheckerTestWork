package com.example.currencycheckertestwork.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencycheckertestwork.constants.DataMode
import com.example.currencycheckertestwork.data.asDomainModel
import com.example.currencycheckertestwork.data.transformToDbModel
import com.example.currencycheckertestwork.data.transformToFavouriteCurrency
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.domain.Result
import com.example.currencycheckertestwork.domain.interaction.GetCurrencyDataUseCase
import com.example.currencycheckertestwork.domain.interaction.RoomUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class SharedViewModel @Inject constructor(
    private val getCurrencyDataUseCase: GetCurrencyDataUseCase,
    private val roomUseCase: RoomUseCase
) : ViewModel() {

    private val errorHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            println("error - $exception")
            errorListener.value = true
        }
    }

    private val _currencyListFromRoom = MutableStateFlow(listOf<Currency>())
    private val _favouriteCurrencyList = MutableStateFlow(listOf<Currency>())

    private val _favouriteListToView = MutableStateFlow(listOf<Currency>())
    val favouriteListToView: StateFlow<List<Currency>> get() = _favouriteListToView.asStateFlow()

    private val _sortedListToView = MutableStateFlow(listOf<Currency>())
    val sortedListToView: StateFlow<List<Currency>> get() = _sortedListToView.asStateFlow()

    val errorListener = MutableStateFlow(false)

    fun loadData() {
        viewModelScope.launch(errorHandler) {
            launch(SupervisorJob()) { loadFavouriteDbList() }
            launch(SupervisorJob()) { loadCurrentDbList() }
            launch(SupervisorJob()) { loadFromServer() }
        }
    }

    private suspend fun loadFromServer() {
        getCurrencyDataUseCase.loadData()
            .collect { result ->
                when (result.status) {
                    Result.Status.SUCCESS -> _sortedListToView.value =
                        result.data?.asDomainModel()?.value ?: listOf()

                    Result.Status.ERROR -> {
                        if (_currencyListFromRoom.value.isNullOrEmpty()) errorListener.value = true
                    }

                    Result.Status.LOADING -> {
                        //show loading
                    }
                }
            }
    }

    private suspend fun loadFavouriteDbList() {
        roomUseCase.getAllFavourite()
            .flowOn(Dispatchers.Main)
            .collect { result ->
                _favouriteCurrencyList.value = result
                _favouriteListToView.value = result
            }
    }

    private suspend fun loadCurrentDbList() {
        roomUseCase.getFullList()
            .flowOn(Dispatchers.Main)
            .collect { result ->
                _currencyListFromRoom.value = result
                _sortedListToView.value = result
            }
    }

    fun finalListToView(sortedMode: DataMode, isFavourite: Boolean) =
        when (isFavourite) {
            true -> _favouriteListToView.value =
                findOutFinalList(sortedMode, _favouriteListToView)

            false -> _sortedListToView.value =
                findOutFinalList(sortedMode, _currencyListFromRoom)

        }

    private fun findOutFinalList(
        sortedMode: DataMode,
        liveDataType: MutableStateFlow<List<Currency>>
    ): List<Currency> =
        when (sortedMode) {
            DataMode.MODE_DEFAULT -> liveDataType.value
            DataMode.MODE_SORTED_BY_VALUE -> liveDataType.value.sortedBy { it.value }
            DataMode.MODE_SORTED_BY_VALUE_VV -> liveDataType.value.sortedByDescending { it.value }
            DataMode.MODE_SORTED_BY_NAME -> liveDataType.value.sortedBy { it.name }
            DataMode.MODE_SORTED_BY_NAME_VV -> liveDataType.value.sortedByDescending { it.name }
        }

    fun insertFavourite(currency: Currency) = viewModelScope.launch(Dispatchers.IO) {
        roomUseCase.saveFavourite(currency.transformToFavouriteCurrency().transformToDbModel())
    }

    fun deleteFavourite(name: String) = viewModelScope.launch(Dispatchers.IO) {
        roomUseCase.deleteFavourite(name)
    }

    fun setSearch(text: String, isFavouriteMode: Boolean) =
        when (isFavouriteMode) {
            true -> {
                val finalList = _favouriteCurrencyList.value.filter {
                    it.name.uppercase(Locale.getDefault())
                        .contains(text.uppercase(Locale.getDefault()))
                }
                _favouriteListToView.value = finalList
            }
            false -> {
                val finalList = _currencyListFromRoom.value.filter {
                    it.name.uppercase(Locale.getDefault())
                        .contains(text.uppercase(Locale.getDefault()))
                }
                _sortedListToView.value = finalList
            }
        }
}