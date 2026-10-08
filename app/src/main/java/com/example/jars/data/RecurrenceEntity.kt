package com.example.jars.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurrences")
data class RecurrenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val type: String = "OTHER",
    val baseAmount: Double = 0.0,
    val percentPerYear: Double = 0.0,
    val startYear: Int = 2026,
    val currentAmount: Double = 0.0
)
