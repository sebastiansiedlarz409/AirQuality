package com.example.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StationHistoryEntity")
class StationHistoryEntity(
    @ColumnInfo(name = "StationId") var StationId: Int,
    @ColumnInfo(name = "StationName") var StationName: String,
    @ColumnInfo(name = "Name") var Name: String,
    @ColumnInfo(name = "Date") var Date: String,
    @ColumnInfo(name = "Index") var Index: String,
    @ColumnInfo(name = "ReadTime") var ReadTime: Long
): Comparable<StationHistoryEntity>
{
    @PrimaryKey(autoGenerate = true) var Id: Int = 0

    override fun compareTo(other: StationHistoryEntity): Int {
        return if(this.ReadTime > other.ReadTime){
            1
        } else{
            -1
        }
    }
}