package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StationHistoryDao {
    @Query("SELECT * FROM StationHistoryEntity WHERE StationId = :id ORDER BY ReadTime DESC")
    fun getAll(id: Int): MutableList<StationHistoryEntity>

    @Insert
    fun insert(stationHistoryEntity: StationHistoryEntity)
}