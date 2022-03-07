package com.example.currencycheckertestwork.domain


data class CurrentCurrency(
    val value: List<Currency>
)

data class Currency(
    val name: String,
    val value: Double
)

data class FavouriteCurrency(
    val name: String,
    val value: Double
)