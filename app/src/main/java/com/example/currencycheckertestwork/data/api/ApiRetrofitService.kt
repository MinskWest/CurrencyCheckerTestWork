package com.example.currencycheckertestwork.data.api

import com.example.currencycheckertestwork.BuildConfig
import com.example.currencycheckertestwork.constants.ACCESS_KEY
import com.example.currencycheckertestwork.constants.FORMAT
import com.example.currencycheckertestwork.data.CurrentCurrencyDTO
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRetrofitService {

    @GET("latest")
    fun getAllCurrencyList(
        @Query(ACCESS_KEY) apiKey: String = BuildConfig.SERVICE_KEY,
        @Query(FORMAT) format: String = BuildConfig.SERVICE_FORMAT,
    ): Single<CurrentCurrencyDTO>

}