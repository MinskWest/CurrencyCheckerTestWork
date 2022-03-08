package com.example.currencycheckertestwork.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencycheckertestwork.data.models.DbCurrentCurrency
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyList(dbCurrentCurrency: DbCurrentCurrency)

    @Query("SELECT * FROM saved_current_currency_list WHERE id = 1 LIMIT 1")
    fun getSavedCurrencyList(): Flow<DbCurrentCurrency?>

}