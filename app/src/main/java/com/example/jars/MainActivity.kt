package com.example.jars

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pinManager = PinManager(this)

        if (!pinManager.hasPin()) {
            showSetPinDialog(pinManager)
        } else {
            showEnterPinDialog(pinManager)
        }
    }

    private fun showSetPinDialog(pm: PinManager) {
        val input = EditText(this).apply {
            hint = "ตั้ง PIN 4-6 หลัก"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                        android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }

        AlertDialog.Builder(this)
            .setTitle("ตั้ง PIN สำหรับแอป")
            .setMessage("ใส่ PIN ที่จะใช้เปิดแอป")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("ตกลง") { _, _ ->
                val pin = input.text.toString()
                if (pin.length < 4) {
                    Toast.makeText(this, "PIN ต้องมี 4-6 หลัก", Toast.LENGTH_SHORT).show()
                    showSetPinDialog(pm)
                } else {
                    pm.savePin(pin)
                    Toast.makeText(this, "ตั้ง PIN แล้ว", Toast.LENGTH_SHORT).show()
                    openQuickActivity()
                }
            }
            .show()
    }

    private fun showEnterPinDialog(pm: PinManager) {
        val input = EditText(this).apply {
            hint = "ใส่ PIN"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                        android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }

        AlertDialog.Builder(this)
            .setTitle("ใส่ PIN เพื่อเข้าแอป")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("ตกลง") { _, _ ->
                if (pm.checkPin(input.text.toString())) {
                    openQuickActivity()
                } else {
                    Toast.makeText(this, "PIN ผิด", Toast.LENGTH_SHORT).show()
                    showEnterPinDialog(pm)
                }
            }
            .setNegativeButton("ออก") { _, _ -> finish() }
            .show()
    }

    private fun openQuickActivity() {
        startActivity(Intent(this, QuickActivity::class.java))
        finish()
    }
}
