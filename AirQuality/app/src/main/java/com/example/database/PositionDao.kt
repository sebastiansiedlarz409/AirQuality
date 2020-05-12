package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PositionDao {
    @Query("SELECT * FROM PositionEntity WHERE StationId LIKE :id")
    fun getAllById(id: Int): MutableList<PositionEntity>

    @Insert
    fun insert(positionEntity: PositionEntity)

    @Query("DELETE FROM PositionEntity WHERE StationId LIKE :id")
    fun deleteAll(id: Int)
}