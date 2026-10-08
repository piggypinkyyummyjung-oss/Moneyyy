package com.example.jars.data

import androidx.room.Entity

@Entity(
    tableName = "month_overrides",
    primaryKeys = ["recurrenceId", "yearMonth"]
)
data class MonthOverrideEntity(
    val recurrenceId: Long = 0,
    val yearMonth: String = "",
    val overrideAmount: Double = 0.0,
    val note: String = ""
)
