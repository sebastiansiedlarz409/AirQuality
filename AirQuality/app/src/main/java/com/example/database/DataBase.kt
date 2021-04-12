package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StationIndexEntity::class, PositionEntity::class, StationHistoryEntity::class], version = 1)
abstract class DataBase : RoomDatabase() {
    abstract fun stationIndexDao(): StationIndexDao
    abstract fun positionDao(): PositionDao
    abstract fun stationHistoryDao(): StationHistoryDao

    companion object{
        private var db: DataBase? = null
        fun getDbInstance(context: Context): DataBase{
            if(db == null){
                db = Room.databaseBuilder(
                    context,
                    DataBase::class.java, "AirQualityDb"
                ).build()
            }
            return db as DataBase
        }
    }
}