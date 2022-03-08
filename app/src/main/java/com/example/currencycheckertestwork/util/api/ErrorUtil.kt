package com.example.currencycheckertestwork.util.api

import com.example.currencycheckertestwork.domain.OurError
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

object ErrorUtils {

    fun parseError(response: Response<*>, retrofit: Retrofit): OurError? {
        val converter =
            retrofit.responseBodyConverter<OurError>(OurError::class.java, arrayOfNulls(0))
        return try {
            converter.convert(response.errorBody()!!)
        } catch (e: IOException) {
            OurError()
        }
    }

}