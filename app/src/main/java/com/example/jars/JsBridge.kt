package com.example.jars

import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.example.jars.data.AppDatabase
import com.example.jars.data.ExpenseEntity
import com.example.jars.data.RecurrenceEntity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class JsBridge(private val context: Context, private val webView: WebView) {

    private val db = AppDatabase.get(context)
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)

    @JavascriptInterface
    fun saveExpense(json: String): Long {
        val e = gson.fromJson(json, ExpenseEntity::class.java)
        val id = db.expenseDao().insert(e)
        refreshAll()
        return id
    }

    @JavascriptInterface
    fun deleteExpense(id: Long) {
        scope.launch {
            db.expenseDao().deleteById(id)
            refreshAll()
        }
    }

    @JavascriptInterface
    fun refreshTotals() {
        refreshAll()
    }

    @JavascriptInterface
    fun refreshTodayList() {
        pushTodayList()
    }

    @JavascriptInterface
    fun saveRecurrence(json: String) {
        scope.launch {
            val r = gson.fromJson(json, RecurrenceEntity::class.java)
            db.recurrenceDao().insert(r)
            pushRecurrences()
        }
    }

    @JavascriptInterface
    fun getRecurrences(): String {
        return gson.toJson(db.recurrenceDao().getAll())
    }

    @JavascriptInterface
    fun getMonthReport(): String {
        val ym = LocalDate.now().toString().substring(0, 7)
        val totalSpent = db.expenseDao().sumMonth(ym) ?: 0.0
        val count = db.expenseDao().countMonth(ym) ?: 0
        val avg = if (count > 0) totalSpent / count else 0.0
        val data = mapOf(
            "totalSpent" to totalSpent,
            "count" to count,
            "avgPerDay" to avg
        )
        return gson.toJson(data)
    }

    @JavascriptInterface
    fun saveSalary(amount: Double) {
        val prefs = context.getSharedPreferences("money_prefs", Context.MODE_PRIVATE)
        prefs.edit().putFloat("salary", amount.toFloat()).apply()
        refreshAll()
    }

    @JavascriptInterface
    fun saveDailyBudget(mode: String, manual: Double) {
        val prefs = context.getSharedPreferences("money_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("daily_mode", mode)
            .putFloat("daily_manual", manual.toFloat())
            .apply()
        refreshAll()
    }

    private fun refreshAll() {
        scope.launch {
            val totals = MoneyEngine.compute(context, db)
            webView.post {
                webView.evaluateJavascript(
                    "window.onTotals(${gson.toJson(totals)})", null
                )
            }
            pushTodayList()
        }
    }

    private fun pushTodayList() {
        scope.launch {
            val today = LocalDate.now().toString()
            val list = db.expenseDao().getByDate(today)
            webView.post {
                webView.evaluateJavascript(
                    "window.onTodayExpenses(${gson.toJson(list)})", null
                )
            }
        }
    }

    private fun pushRecurrences() {
        scope.launch {
            val list = db.recurrenceDao().getAll()
            webView.post {
                webView.evaluateJavascript(
                    "window.onRecurrences(${gson.toJson(list)})", null
                )
            }
        }
    }
}
