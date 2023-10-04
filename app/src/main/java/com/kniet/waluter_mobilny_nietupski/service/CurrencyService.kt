package com.kniet.waluter_mobilny_nietupski.service

import com.kniet.waluter_mobilny_nietupski.model.CurrenciesItem
import retrofit2.Call
import retrofit2.http.GET

interface CurrencyService {

    @GET("a/?format=json")
    fun getCurrencyList () : Call<List<CurrenciesItem>>
}