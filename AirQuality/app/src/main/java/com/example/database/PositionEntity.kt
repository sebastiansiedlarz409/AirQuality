package com.example.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PositionEntity")
class PositionEntity(
    @ColumnInfo(name = "PosId") var SensorId: Int,
    @ColumnInfo(name = "StationId") var StationId: Int,
    @ColumnInfo(name = "ParamName") var ParamName: String,
    @ColumnInfo(name = "ParamFormula") var ParamFormula: String,
    @ColumnInfo(name = "ParamCode") var ParamCode: String,
    @ColumnInfo(name = "Value") var Value: String
)
{
    @PrimaryKey(autoGenerate = true) var Id: Int = 0
}