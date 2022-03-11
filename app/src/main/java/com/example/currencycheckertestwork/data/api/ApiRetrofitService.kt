package com.example.currencycheckertestwork.data.api

import com.example.currencycheckertestwork.BuildConfig
import com.example.currencycheckertestwork.constants.ACCESS_KEY
import com.example.currencycheckertestwork.constants.FORMAT
import com.example.currencycheckertestwork.data.CurrentCurrencyDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRetrofitService {

    @GET("latest")
    suspend fun getAllCurrencyList(
        @Query(ACCESS_KEY) apiKey: String = BuildConfig.SERVICE_KEY,
        @Query(FORMAT) format: String = BuildConfig.SERVICE_FORMAT,
    ): Response<CurrentCurrencyDTO>

}