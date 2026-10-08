package com.example.jars

import android.content.Context
import java.time.LocalDate
import java.time.YearMonth

data class Totals(
    val jarBalance: Double,
    val todayLeft: Double,
    val todaySpent: Double,
    val dailyBudget: Double
)

object MoneyEngine {

    fun compute(context: Context): Totals {
        val prefs = context.getSharedPreferences("money_prefs", Context.MODE_PRIVATE)
        val today = LocalDate.now()
        val ym = YearMonth.from(today)
        val todayStr = today.toString()

        val salary = prefs.getFloat("salary", 0f).toDouble()
        val totalSpentAll = prefs.getFloat("totalSpentAll", 0f).toDouble()
        val jar = salary - totalSpentAll

        val mode = prefs.getString("daily_mode", "auto") ?: "auto"
        val manual = prefs.getFloat("daily_manual", 0f).toDouble()
        val daily = if (mode == "manual" && manual > 0) manual
        else {
            val daysInMonth = ym.lengthOfMonth()
            val dayOfMonth = today.dayOfMonth
            val daysLeft = (daysInMonth - dayOfMonth + 1).coerceAtLeast(1)
            (jar / daysLeft).coerceAtLeast(0.0)
        }

        val todaySpent = prefs.getFloat("todaySpent_$todayStr", 0f).toDouble()
        val todayLeft = (daily - todaySpent).coerceAtLeast(0.0)

        return Totals(
            jarBalance = jar,
            todayLeft = todayLeft,
            todaySpent = todaySpent,
            dailyBudget = daily
        )
    }
}
