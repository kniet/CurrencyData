package com.kniet.waluter_mobilny_nietupski.model

data class CurrenciesItem(
    val effectiveDate: String,
    val rates: List<Rate>,
)