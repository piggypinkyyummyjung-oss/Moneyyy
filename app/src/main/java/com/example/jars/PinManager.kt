package com.example.jars

import android.content.Context
import android.content.SharedPreferences

class PinManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("pin_prefs", Context.MODE_PRIVATE)

    fun hasPin(): Boolean = prefs.contains("pin")

    fun savePin(pin: String) {
        prefs.edit().putString("pin", pin).apply()
    }

    fun checkPin(pin: String): Boolean {
        return prefs.getString("pin", "") == pin
    }

    fun clearPin() {
        prefs.edit().remove("pin").apply()
    }
}
