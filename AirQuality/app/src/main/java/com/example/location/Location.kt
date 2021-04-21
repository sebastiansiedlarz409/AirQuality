package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat.getSystemService
import com.example.database.StationIndexEntity


class Location {
    @SuppressLint("MissingPermission")
    fun getDistance(station: StationIndexEntity, context: Context) : Double {
        var lat: Double = station.Lat.toDouble()
        var lon: Double = station.Lon.toDouble()
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val location: Location? = lm!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val longitude: Double = location?.getLongitude() ?: 0.0
        val latitude: Double = location?.getLatitude() ?: 0.0

        return Math.sqrt((lat-latitude)*(lat-latitude) - (lon-longitude)*(lon-longitude))
    }

    fun getNearestStation(stations: MutableList<StationIndexEntity>, context: Context): StationIndexEntity?{
        var min = Double.MAX_VALUE;
        var best: StationIndexEntity? = null
        for(station in stations){
            var result = getDistance(station, context)

            if(result < min){
                min = result
                best = station
            }
        }

        return best
    }
}