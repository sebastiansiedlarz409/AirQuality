package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PositionDao {
    @Query("SELECT * FROM PositionEntity")
    fun getAll(): MutableList<PositionEntity>

    @Insert
    fun insert(positionEntity: PositionEntity)

    @Query("DELETE FROM PositionEntity")
    fun deleteAll()
}