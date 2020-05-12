package com.example.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StationIndexEntity")
class StationIndexEntity (
    @ColumnInfo(name = "StationId") var StationId: Int,
    @ColumnInfo(name = "StationName") var StationName: String,
    @ColumnInfo(name = "CityId") var CityId: Int,
    @ColumnInfo(name = "Name") var Name: String,
    @ColumnInfo(name = "CommuneName") val CommuneName: String,
    @ColumnInfo(name = "DistrictName") var DistrictName: String,
    @ColumnInfo(name = "ProvinceName") var ProvinceName: String,
    @ColumnInfo(name = "Lat") var Lat: String,
    @ColumnInfo(name = "Lon") var Lon: String,
    @ColumnInfo(name = "Date") var Date: String,
    @ColumnInfo(name = "Index") var Index: String
): Comparable<StationIndexEntity>
{
    @PrimaryKey(autoGenerate = true) var Id: Int = 0

    override fun compareTo(other: StationIndexEntity): Int {
        return if(this.Name > other.Name){
            1
        } else{
            -1
        }
    }
}