package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.example.database.StationIndexEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class Location : LocationListener {
    private var refreshed: Boolean = false
    private var longitude: Double? = null
    private var latitude: Double? = null
    private var listItems: ArrayList<StationIndexEntity> = arrayListOf()
    private var refreshViewCallback: suspend  (list: ArrayList<StationIndexEntity>) -> Unit

    @SuppressLint("MissingPermission")
    constructor(
        context: Context,
        callback: suspend (list: ArrayList<StationIndexEntity>) -> Unit,
        listItems: ArrayList<StationIndexEntity>
    ){
        refreshViewCallback = callback
        this.listItems = listItems

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        lm!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    private fun getDistance(station: StationIndexEntity, context: Context) : Double {
        val lat: Double = station.Lat.toDouble()
        val lon: Double = station.Lon.toDouble()

        Log.d(longitude.toString(), latitude.toString())

        return Math.sqrt((lat- latitude!!)*(lat- latitude!!) - (lon- longitude!!)*(lon- longitude!!))
    }

    fun getNearestStation(stations: MutableList<StationIndexEntity>, context: Context): StationIndexEntity?{
        if(longitude == null || latitude == null){
            return null
        }

        var min = Double.MAX_VALUE
        var best: StationIndexEntity? = null
        for(station in stations){
            val result = getDistance(station, context)
            Log.d(station.Name, result.toString())

            if(result < min){
                min = result
                best = station
            }
        }

        return best
    }

    override fun onLocationChanged(location: Location?) {
        longitude = location?.longitude ?: 0.0
        latitude = location?.latitude ?: 0.0

        CoroutineScope(Dispatchers.Default).launch {
            if(!refreshed) {
                refreshViewCallback(listItems)
                refreshed = true
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }
}