package com.example.jars

import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

class JsBridge(private val context: Context, private val webView: WebView) {

    private val gson = Gson()
    private val prefs = context.getSharedPreferences("money_prefs", Context.MODE_PRIVATE)

    @JavascriptInterface
    fun saveExpense(json: String): Long {
        val map: Map<String, Any> = gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)
        val amount = (map["amount"] as? Double) ?: 0.0
        val date = map["date"] as? String ?: LocalDate.now().toString()
        val name = map["name"] as? String ?: ""
        val category = map["category"] as? String ?: "อื่นๆ"

        val id = System.currentTimeMillis()

        // อ่านรายการเดิม
        val listJson = prefs.getString("expenses", "[]") ?: "[]"
        val list: MutableList<Map<String, Any>> = gson.fromJson(
            listJson,
            object : TypeToken<MutableList<Map<String, Any>>>() {}.type
        )
        list.add(mapOf(
            "id" to id,
            "name" to name,
            "amount" to amount,
            "date" to date,
            "category" to category,
            "timestamp" to id
        ))
        prefs.edit().putString("expenses", gson.toJson(list)).apply()

        // อัปเดตยอดรวม
        val totalSpentAll = prefs.getFloat("totalSpentAll", 0f) + amount.toFloat()
        val todaySpentKey = "todaySpent_$date"
        val todaySpent = prefs.getFloat(todaySpentKey, 0f) + amount.toFloat()
        prefs.edit()
            .putFloat("totalSpentAll", totalSpentAll)
            .putFloat(todaySpentKey, todaySpent)
            .apply()

        refreshAll()
        return id
    }

    @JavascriptInterface
    fun deleteExpense(id: Long) {
        val listJson = prefs.getString("expenses", "[]") ?: "[]"
        val list: MutableList<Map<String, Any>> = gson.fromJson(
            listJson,
            object : TypeToken<MutableList<Map<String, Any>>>() {}.type
        )
        val item = list.find { (it["id"] as? Double)?.toLong() == id }
        if (item != null) {
            val amount = (item["amount"] as? Double) ?: 0.0
            val date = item["date"] as? String ?: ""
            val totalSpentAll = prefs.getFloat("totalSpentAll", 0f) - amount.toFloat()
            val todaySpentKey = "todaySpent_$date"
            val todaySpent = prefs.getFloat(todaySpentKey, 0f) - amount.toFloat()
            prefs.edit()
                .putFloat("totalSpentAll", totalSpentAll)
                .putFloat(todaySpentKey, todaySpent)
                .apply()
        }
        list.removeAll { (it["id"] as? Double)?.toLong() == id }
        prefs.edit().putString("expenses", gson.toJson(list)).apply()
        refreshAll()
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
        val listJson = prefs.getString("recurrences", "[]") ?: "[]"
        val list: MutableList<Map<String, Any>> = gson.fromJson(
            listJson,
            object : TypeToken<MutableList<Map<String, Any>>>() {}.type
        )
        val newItem: Map<String, Any> = gson.fromJson(
            json,
            object : TypeToken<Map<String, Any>>() {}.type
        )
        list.add(newItem)
        prefs.edit().putString("recurrences", gson.toJson(list)).apply()
        pushRecurrences()
    }

    @JavascriptInterface
    fun getRecurrences(): String {
        return prefs.getString("recurrences", "[]") ?: "[]"
    }

    @JavascriptInterface
    fun getMonthReport(): String {
        val ym = LocalDate.now().toString().substring(0, 7)
        val listJson = prefs.getString("expenses", "[]") ?: "[]"
        val list: List<Map<String, Any>> = gson.fromJson(
            listJson,
            object : TypeToken<List<Map<String, Any>>>() {}.type
        )
        val monthItems = list.filter { (it["date"] as? String)?.startsWith(ym) == true }
        val totalSpent = monthItems.sumOf { (it["amount"] as? Double) ?: 0.0 }
        val count = monthItems.size
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
        prefs.edit().putFloat("salary", amount.toFloat()).apply()
        refreshAll()
    }

    @JavascriptInterface
    fun saveDailyBudget(mode: String, manual: Double) {
        prefs.edit()
            .putString("daily_mode", mode)
            .putFloat("daily_manual", manual.toFloat())
            .apply()
        refreshAll()
    }

    private fun refreshAll() {
        val totals = MoneyEngine.compute(context)
        webView.post {
            webView.evaluateJavascript(
                "window.onTotals(${gson.toJson(totals)})", null
            )
        }
        pushTodayList()
    }

    private fun pushTodayList() {
        val today = LocalDate.now().toString()
        val listJson = prefs.getString("expenses", "[]") ?: "[]"
        val list: List<Map<String, Any>> = gson.fromJson(
            listJson,
            object : TypeToken<List<Map<String, Any>>>() {}.type
        )
        val todayList = list.filter { it["date"] == today }
        webView.post {
            webView.evaluateJavascript(
                "window.onTodayExpenses(${gson.toJson(todayList)})", null
            )
        }
    }

    private fun pushRecurrences() {
        val listJson = prefs.getString("recurrences", "[]") ?: "[]"
        webView.post {
            webView.evaluateJavascript(
                "window.onRecurrences($listJson)", null
            )
        }
    }
}
