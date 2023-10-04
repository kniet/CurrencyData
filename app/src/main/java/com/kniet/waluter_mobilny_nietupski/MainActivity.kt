package com.kniet.waluter_mobilny_nietupski

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.setPadding
import com.kniet.waluter_mobilny_nietupski.model.CurrenciesItem
import com.kniet.waluter_mobilny_nietupski.service.CurrencyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Collections

class MainActivity : ComponentActivity() {
    private val URL = "https://api.nbp.pl/api/exchangerates/tables/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getAllCurrencies()
    }

    private fun getAllCurrencies() {
        val builder = Retrofit.Builder().baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyService::class.java)

        builder.getCurrencyList().enqueue(object : Callback<List<CurrenciesItem>> {
            override fun onResponse(
                call: Call<List<CurrenciesItem>>,
                response: Response<List<CurrenciesItem>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        for (currency in it) {
                            Log.i("CHECK_RESPONSE", "onResposnse: ${currency.effectiveDate}")
                        }
                        test(it)
                    }
                }
            }
            override fun onFailure(call: Call<List<CurrenciesItem>>, t: Throwable) {
                Log.i("CHECK_RESPONSE", "onFailure: ${t.message}")
            }
        })
    }

    private fun test(list: List<CurrenciesItem>) {
        for (i in 0 until list.get(0).rates.count()) {
            val table = findViewById<TableLayout>(R.id.kursyTable)
            val row = TableRow(this)
            val column1 = TextView(this)
            val column2 = TextView(this)
            val column3 = TextView(this)

            column1.text = list.get(0).rates.get(i).code
            column1.setBackgroundResource(R.drawable.drawable_skrot_waluty)
            column1.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                0.2f
            )
            column1.textAlignment = View.TEXT_ALIGNMENT_CENTER
            column1.setPadding(15)
            column1.setTypeface(null, Typeface.BOLD)
            row.addView(column1)

            column2.text = list.get(0).rates.get(i).currency
            column2.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )
            column2.setBackgroundResource(R.drawable.drawable_nazwa_waluty)
            column2.textAlignment = View.TEXT_ALIGNMENT_CENTER
            column2.setPadding(15)
            column2.setTypeface(null, Typeface.BOLD)
            row.addView(column2)

            column3.text = list.get(0).rates.get(i).mid.toString()
            column3.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                0.4f
            )
            column3.setBackgroundResource(R.drawable.drawabale_wartosc_waluty)
            column3.textAlignment = View.TEXT_ALIGNMENT_CENTER
            column3.setPadding(15)
            column3.setTypeface(null, Typeface.BOLD)
            row.addView(column3)

            table.addView(row, TableLayout.LayoutParams.MATCH_PARENT)
        }
    }
}
