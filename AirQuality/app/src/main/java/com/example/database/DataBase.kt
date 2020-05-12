package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StationIndexEntity::class, PositionEntity::class], version = 1)
abstract class DataBase : RoomDatabase() {
    abstract fun stationIndexDao(): StationIndexDao
    abstract fun positionDao(): PositionDao
}