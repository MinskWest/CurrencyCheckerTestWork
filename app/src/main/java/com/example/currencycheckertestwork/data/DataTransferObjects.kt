package com.example.currencycheckertestwork.data

import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.domain.CurrentCurrency
import com.example.currencycheckertestwork.domain.FavouriteCurrency
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentCurrencyDTO(
    @Json(name = "rates") val loadValue: Map<String, Double>
)

fun CurrentCurrencyDTO.asDomainModel(): CurrentCurrency =
    CurrentCurrency(
        loadValue.map {
            Currency(it.key, it.value)
        }
    )

fun CurrentCurrency.transformToDbModel(): DbCurrentCurrency =
    DbCurrentCurrency(
        1L,
        savedValue = value
    )

fun FavouriteCurrency.transformToDbModel(): DbFavouriteCurrency =
    DbFavouriteCurrency(
        name = name,
        value = value
    )

fun Currency.transformToFavouriteCurrency(): FavouriteCurrency =
    FavouriteCurrency(
        name = name,
        value = value
    )

fun FavouriteCurrency.transformToCurrency(): Currency =
    Currency(
        name = name,
        value = value
    )




