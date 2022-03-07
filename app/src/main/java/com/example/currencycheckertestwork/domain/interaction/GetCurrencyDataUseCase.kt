package com.example.currencycheckertestwork.domain.interaction

import com.example.currencycheckertestwork.data.CurrentCurrencyDTO
import com.example.currencycheckertestwork.domain.CommonRepository
import io.reactivex.Single
import javax.inject.Inject

class GetCurrencyDataUseCase @Inject constructor(
    private val commonRepository: CommonRepository
) {

    fun loadData(): Single<CurrentCurrencyDTO> = commonRepository.loadDataByRetrofit()
}