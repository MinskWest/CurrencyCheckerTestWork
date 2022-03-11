package com.example.currencycheckertestwork.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.currencycheckertestwork.constants.DB_FAVOURITES
import com.example.currencycheckertestwork.constants.FAV_CURRENCY_NAME
import com.example.currencycheckertestwork.constants.FAV_CURRENCY_VALUE

@Entity(tableName = DB_FAVOURITES)
data class DbFavouriteCurrency(
    @PrimaryKey
    @ColumnInfo(name = FAV_CURRENCY_NAME)
    val name: String,
    @ColumnInfo(name = FAV_CURRENCY_VALUE)
    val value: Double
)
