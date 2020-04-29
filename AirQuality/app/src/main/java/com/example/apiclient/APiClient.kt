package com.example.apiclient

import android.util.Log
import com.example.models.Position
import com.example.models.Station
import com.example.models.StationIndex
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject

class APIClient{

    private val urlStation: String = "https://api.gios.gov.pl/pjp-api/rest/station/findAll"
    private val urlStationIndex: String = "https://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/"
    private val urlPositions: String = "https://api.gios.gov.pl/pjp-api/rest/station/sensors/"
    private val client = OkHttpClient()

    fun getAllStation() : String? {

        val request = Request.Builder()
            .url(this.urlStation)
            .build()

        return client.newCall(request).execute().body()?.string()

    }

    suspend fun getAllStationList(data: String?) : MutableList<Station> {

        val jsonArray = JSONArray(data)
        val count: Int = jsonArray.length()
        val stations: MutableList<Station> = mutableListOf()

        for(i in 1..count){
            val jsonObject: JSONObject = jsonArray.getJSONObject(i-1)

            val id: Int = jsonObject.getInt("id")
            val stationName = jsonObject.getString("stationName")
            val lat = jsonObject.getString("gegrLat")
            val lon = jsonObject.getString("gegrLon")

            val jsonSubObject: JSONObject = jsonObject.getJSONObject("city")
            val cityId: Int = jsonSubObject.getInt("id")
            val name: String = jsonSubObject.getString("name")

            val jsonSecondSubObject: JSONObject = jsonSubObject.getJSONObject("commune")
            val communeName: String = jsonSecondSubObject.getString("communeName")
            val districtName: String = jsonSecondSubObject.getString("districtName")
            val provinceName: String = jsonSecondSubObject.getString("provinceName")

            stations.add(Station(id, stationName, cityId, name, communeName, districtName, provinceName, lat, lon))
        }

        //experiment
        Log.d(count.toString(), "ad")
        val jobs: MutableList<Deferred<Unit>> = mutableListOf()
        for(item in stations){
            val job = CoroutineScope(IO).async {
                Log.d(item.Id.toString(), "asda")
                val index: StationIndex = getStationIndexData(getStationIndex(item.Id))
                item.Index = index
            }
            jobs.add(job)
        }

        jobs.forEach {
            job -> job.await()
        }

        return stations
    }

    fun getStationIndex(index: Int): String? {

        val request = Request.Builder()
            .url(this.urlStationIndex + index)
            .build()

        return client.newCall(request).execute().body()?.string()

    }

    fun getStationIndexData(data: String?) : StationIndex {

        val jsonObject = JSONObject(data)
        val id = jsonObject.getInt("id")
        val date = jsonObject.getString("stCalcDate")
        val jsonSubObject: JSONObject = jsonObject.getJSONObject("stIndexLevel")
        val index = jsonSubObject.getString("indexLevelName")

        return StationIndex(id, date, index)
    }

    fun getPositions(index: Int): String? {

        if(index == 0){
            return null
        }

        val request = Request.Builder()
            .url(this.urlPositions + index)
            .build()

        return client.newCall(request).execute().body()?.string()
    }

    fun getPositionsData(data: String?) : MutableList<Position> {

        val positions: MutableList<Position> = mutableListOf()

        if(data.isNullOrEmpty()){
            return positions
        }

        val jsonArray = JSONArray(data)
        val count: Int = jsonArray.length()

        for(i in 1..count){
            val jsonObject = jsonArray.getJSONObject(i-1)
            val id = jsonObject.getInt("id")
            val stationId = jsonObject.getInt("stationId")
            val jsonSubObject: JSONObject = jsonObject.getJSONObject("param")
            val paramName = jsonSubObject.getString("paramName")
            val paramFormula = jsonSubObject.getString("paramFormula")
            val paramCode = jsonSubObject.getString("paramCode")

            positions.add(Position(id, stationId, paramName, paramFormula, paramCode))
        }

        return positions
    }
}