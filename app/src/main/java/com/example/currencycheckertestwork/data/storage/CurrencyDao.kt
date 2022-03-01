package com.example.currencycheckertestwork.data.storage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import io.reactivex.Completable

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrencyList(dbCurrentCurrency: DbCurrentCurrency): Completable

    @Query("SELECT * FROM saved_current_currency_list WHERE id = 1 LIMIT 1")
    fun getSavedCurrencyList(): LiveData<DbCurrentCurrency>

}