package com.example.currencycheckertestwork.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_items")
data class DbFavouriteCurrency(
    @PrimaryKey
    @ColumnInfo(name ="currencyName")
    val name: String
)
