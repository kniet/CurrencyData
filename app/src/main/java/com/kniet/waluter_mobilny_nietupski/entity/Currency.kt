package com.kniet.waluter_mobilny_nietupski.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "Currency")
data class Currency(

    @ColumnInfo(name = "code")
    val currencyCode: String,

    @ColumnInfo(name = "currency")
    val currency: String,

    @ColumnInfo(name = "mid")
    val mid: Double,

    @ColumnInfo(name = "date")
    val date: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
