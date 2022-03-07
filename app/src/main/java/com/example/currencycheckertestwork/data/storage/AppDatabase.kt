package com.example.currencycheckertestwork.data.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency

@Database(entities = [DbCurrentCurrency::class, DbFavouriteCurrency::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    abstract fun favouriteCurrencyDao(): FavouriteCurrencyDao
}