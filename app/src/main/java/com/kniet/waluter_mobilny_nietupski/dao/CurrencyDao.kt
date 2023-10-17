package com.kniet.waluter_mobilny_nietupski.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kniet.waluter_mobilny_nietupski.entity.Currency
import com.kniet.waluter_mobilny_nietupski.model.CurrenciesItem

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM Currency")
    fun getAll(): List<Currency>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(currencies: Currency)

    @Query("DELETE FROM Currency")
    fun nukeTable();
}