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
import java.util.*

class Location : LocationListener {
    private var callUI: Boolean = false
    private var refreshed: Boolean = false
    public var longitude: Double? = null
    public var latitude: Double? = null
    private var listItems: ArrayList<StationIndexEntity> = arrayListOf()
    private lateinit var refreshViewCallback: suspend  (list: ArrayList<StationIndexEntity>) -> Unit

    @SuppressLint("MissingPermission")
    constructor(
        context: Context,
        callUI: Boolean
    ){
        this.callUI = callUI

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        //lm!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2f, this)
        lm!!.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null)
    }

    @SuppressLint("MissingPermission")
    constructor(
        context: Context,
        callback: suspend (list: ArrayList<StationIndexEntity>) -> Unit,
        listItems: ArrayList<StationIndexEntity>,
        callUI: Boolean
    ){
        refreshViewCallback = callback
        this.listItems = listItems
        this.callUI = callUI

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        lm!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.01f, this)
    }

    private fun getDistance(station: StationIndexEntity, context: Context) : Double {
        val lat: Double = station.Lat.toDouble()
        val lon: Double = station.Lon.toDouble()

        return Math.sqrt((lat- latitude!!)*(lat- latitude!!) + (lon- longitude!!)*(lon- longitude!!))
    }

    fun getNearestStation(stations: MutableList<StationIndexEntity>, context: Context): StationIndexEntity?{
        if(longitude == null || latitude == null){
            return null
        }

        var min = Double.MAX_VALUE
        var best: StationIndexEntity? = null
        for(station in stations){
            val result = getDistance(station, context)

            if(result < min){
                min = result
                best = station
            }
        }

        val sharedPreferences = context.getSharedPreferences("AiqQualitySP", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("Nearest", best!!.StationId).apply()

        return best
    }

    override fun onLocationChanged(location: Location?) {
        longitude = location?.longitude ?: 0.0
        latitude = location?.latitude ?: 0.0

        if(callUI) {
            CoroutineScope(Dispatchers.Default).launch {
                if(!refreshed) {
                    refreshViewCallback(listItems)
                    refreshed = true
                }
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