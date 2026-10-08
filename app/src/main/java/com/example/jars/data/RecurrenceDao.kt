package com.example.jars.data

import androidx.room.*

@Dao
interface RecurrenceDao {

    @Insert
    suspend fun insert(r: RecurrenceEntity): Long

    @Update
    suspend fun update(r: RecurrenceEntity)

    @Delete
    suspend fun delete(r: RecurrenceEntity)

    @Query("SELECT * FROM recurrences ORDER BY id")
    suspend fun getAll(): List<RecurrenceEntity>

    @Query("SELECT * FROM recurrences WHERE id = :id")
    suspend fun getById(id: Long): RecurrenceEntity?
}
