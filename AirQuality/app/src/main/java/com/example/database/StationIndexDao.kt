package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StationIndexDao {
    @Query("SELECT * FROM StationIndexEntity")
    fun getAll(): MutableList<StationIndexEntity>

    @Insert
    fun insert(stationIndexEntity: StationIndexEntity)

    @Query("DELETE FROM StationIndexEntity")
    fun deleteAll()
}