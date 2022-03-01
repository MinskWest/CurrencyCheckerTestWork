package com.example.currencycheckertestwork.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.currencycheckertestwork.data.storage.CurrencyConverter
import com.example.currencycheckertestwork.domain.Currency

@Entity(tableName = "saved_current_currency_list")
@TypeConverters(CurrencyConverter::class)
data class DbCurrentCurrency(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "ourCurrencyList")
    val savedValue: List<Currency>
)
