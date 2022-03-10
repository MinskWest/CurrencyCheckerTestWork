package com.example.currencycheckertestwork.domain.interaction

import com.example.currencycheckertestwork.data.CurrentCurrencyDTO
import com.example.currencycheckertestwork.domain.CommonRepository
import com.example.currencycheckertestwork.domain.Result
import kotlinx.coroutines.flow.Flow

class GetCurrencyDataUseCase(private val commonRepository: CommonRepository) {

    suspend fun loadData(): Flow<Result<CurrentCurrencyDTO>> =
        commonRepository.loadDataByRetrofit()

}