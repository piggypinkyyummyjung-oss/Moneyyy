package com.example.jars.data

import androidx.room.*

@Dao
interface MonthOverrideDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(o: MonthOverrideEntity)

    @Query("SELECT * FROM month_overrides WHERE recurrenceId = :id AND yearMonth = :ym")
    suspend fun get(id: Long, ym: String): MonthOverrideEntity?

    @Query("DELETE FROM month_overrides WHERE recurrenceId = :id AND yearMonth = :ym")
    suspend fun delete(id: Long, ym: String)
}
