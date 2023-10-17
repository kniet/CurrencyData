package com.kniet.waluter_mobilny_nietupski

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.setPadding
import com.kniet.waluter_mobilny_nietupski.configuration.AppDatabase
import com.kniet.waluter_mobilny_nietupski.dao.CurrencyDao
import com.kniet.waluter_mobilny_nietupski.entity.Currency
import com.kniet.waluter_mobilny_nietupski.model.CurrenciesItem
import com.kniet.waluter_mobilny_nietupski.model.Rate
import com.kniet.waluter_mobilny_nietupski.service.CurrencyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private val URL = "https://api.nbp.pl/api/exchangerates/tables/"
    lateinit var dao: CurrencyDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getAllCurrencies()
        val db = AppDatabase.getDatabase(this)
        dao = db.currencyDao()

        val button = findViewById<ImageButton>(R.id.refreshButton)
        button.setOnClickListener {
            findViewById<TableLayout>(R.id.kursyTable).removeAllViews()
            getAllCurrencies()
        }
    }

    private fun getAllCurrencies() {
        val builder = buildService()

        builder?.getCurrencyList()?.enqueue(object : Callback<List<CurrenciesItem>> {
            override fun onResponse(
                call: Call<List<CurrenciesItem>>,
                response: Response<List<CurrenciesItem>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        toggleOffError()
                        dao.nukeTable()
                        it.flatMap { currency ->
                            currency.rates.map { rates ->
                                Currency(rates.code, rates.currency, rates.mid, currency.effectiveDate)
                            }
                        }.forEach { dataToSave ->
                            dao.insert(dataToSave)
                        }
                        displayData(it)
                    }
                } else {
                    handleError()
                }
            }

            override fun onFailure(call: Call<List<CurrenciesItem>>, t: Throwable) {
                val currencyList: List<Currency>? = dao.getAll()
                val currenciesItemList = mutableListOf<CurrenciesItem>()

                if (currencyList != null) {
                    val rates = currencyList.map { t -> Rate(t.currencyCode, t.currency, t.mid) }
                    currenciesItemList.add(CurrenciesItem(currencyList[0].date, rates))
                } else {
                    handleError()
                }
            }
        })
    }

    private fun toggleOffError() {
        val data = findViewById<TextView>(R.id.lastUpdate)
        data.visibility = View.VISIBLE
        val errorMessage = findViewById<TextView>(R.id.errorMessage)
        errorMessage.visibility = View.GONE
    }
    private fun buildService() :CurrencyService? {
        try {
            val builder = Retrofit.Builder().baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return builder.create(CurrencyService::class.java)
        } catch (e: IllegalArgumentException) {
            handleError()
            return null
        }
    }

    private fun handleError() {
        val data = findViewById<TextView>(R.id.lastUpdate)
        data.visibility = View.GONE
        val errorMessage = findViewById<TextView>(R.id.errorMessage)
        errorMessage.visibility = View.VISIBLE
    }

    private fun displayData(list: List<CurrenciesItem>) {
        val data = findViewById<TextView>(R.id.lastUpdate)

        data.text = "Aktualizacja: ${list.get(0).effectiveDate}"

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

            val mid = list.get(0).rates.get(i).mid
            column3.text = String.format("%.4f", mid)
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
