package com.example.jars.data

import androidx.room.*
import java.time.LocalDate

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(e: ExpenseEntity): Long

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM expenses WHERE date = :d ORDER BY timestamp DESC")
    fun getToday(d: String = LocalDate.now().toString()): List<ExpenseEntity>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE date = :d")
    fun sumToday(d: String = LocalDate.now().toString()): Double?

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses")
    suspend fun sumAll(): Double

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE date LIKE :monthPrefix || '%'")
    suspend fun sumMonth(monthPrefix: String = LocalDate.now().toString().substring(0, 7)): Double?

    @Query("SELECT COUNT(*) FROM expenses WHERE date LIKE :monthPrefix || '%'")
    suspend fun countMonth(monthPrefix: String = LocalDate.now().toString().substring(0, 7)): Int?
}
