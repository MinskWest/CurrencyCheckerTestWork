package com.example.currencycheckertestwork.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.currencycheckertestwork.constants.DB_CURRENT_CURRENCY
import com.example.currencycheckertestwork.constants.OUR_CURRENCY_LIST
import com.example.currencycheckertestwork.data.storage.CurrencyConverter
import com.example.currencycheckertestwork.domain.Currency

@Entity(tableName = DB_CURRENT_CURRENCY)
@TypeConverters(CurrencyConverter::class)
data class DbCurrentCurrency(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = OUR_CURRENCY_LIST)
    val savedValue: List<Currency>
)
