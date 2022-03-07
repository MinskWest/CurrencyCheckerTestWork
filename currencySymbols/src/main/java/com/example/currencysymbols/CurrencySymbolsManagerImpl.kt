package com.example.currencysymbols

import android.content.Context
import java.util.*
import javax.inject.Inject

class CurrencySymbolsManagerImpl @Inject constructor(
    private val context: Context
) : CurrencySymbolsManager {

    private val currentLocal by lazy { context.resources.configuration.locale }

    override fun getSymbol(currencyName: String): String =
        Currency.getInstance(currencyName).getSymbol(currentLocal)

}