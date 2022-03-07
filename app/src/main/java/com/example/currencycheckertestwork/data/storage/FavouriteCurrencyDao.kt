package com.example.currencycheckertestwork.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencycheckertestwork.data.models.DbFavouriteCurrency
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface FavouriteCurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavouriteCurrency(dbFavouriteCurrency: DbFavouriteCurrency): Completable

    @Query("DELETE FROM favourite_items WHERE favCurrencyName=:name")
    fun deleteFavouriteCurrency(name: String): Completable

    @Query("SELECT * FROM favourite_items")
    fun getFavouriteCurrencyList(): Flowable<List<DbFavouriteCurrency>>

}