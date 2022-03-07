package com.example.currencycheckertestwork.presentation.viewmodels

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
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*
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
            currentCurrencyList.clear()
            currentCurrencyList.addAll(it)
            sortedListToView.onNext(it)
        }
    val favouriteCurrency: Observable<Unit> = roomUseCase.getAllFavourite()
        .map {
            favouriteCurrencyList.clear()
            favouriteCurrencyList.addAll(it)
            favouriteListToView.onNext(it)
        }

    fun loadData() {
        getCurrencyDataUseCase.loadData()
            .doOnSuccess { currentCurrencyDTO ->
                loadCurrent(currentCurrencyDTO)
            }
            .subscribe()
    }

    private fun loadCurrent(currentCurrencyDTO: CurrentCurrencyDTO) {
        if (currentCurrencyDTO.loadValue.containsKey(RETROFIT_LOAD_ERROR)) {
            val currentList = listOf<Currency>()
            if (currentList.isEmpty()) {
                // can't load and haven't saved value - view no internet popup
                errorListener.onNext(true)
            } else {
                // view saved version
                sortedListToView.onNext(currentList)
            }
        } else {
            //update data in room
            roomUseCase.saveInRoom(currentCurrencyDTO.asDomainModel().transformToDbModel())
                .doOnComplete {
                    sortedListToView.onNext(currentCurrencyDTO.asDomainModel().value)
                }
                .subscribe()
        }
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

    fun insertFavourite(currency: Currency): Completable =
        roomUseCase.saveFavourite(currency.transformToFavouriteCurrency().transformToDbModel())

    fun deleteFavourite(name: String): Completable = roomUseCase.deleteFavourite(name)

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