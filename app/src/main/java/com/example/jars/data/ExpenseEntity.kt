package com.example.jars.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val category: String = "อื่นๆ",
    val timestamp: Long = System.currentTimeMillis()
)
