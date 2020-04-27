package com.example.apiclient

import com.example.models.Station
import com.example.models.StationIndex
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject

class APIClient{

    private val urlStation: String = "https://api.gios.gov.pl/pjp-api/rest/station/findAll"
    private val urlStationIndex: String = "https://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/"
    private val client = OkHttpClient()

    fun getAllStation() : String? {

        val request = Request.Builder()
            .url(this.urlStation)
            .build()

        return client.newCall(request).execute().body()?.string()

    }

    fun getAllStationList(data: String?) : MutableList<Station> {

        val jsonArray = JSONArray(data)
        val count: Int = jsonArray.length()
        val stations: MutableList<Station> = mutableListOf()

        for(i in 1..count){
            val jsonObject: JSONObject = jsonArray.getJSONObject(i-1)

            val id: Int = jsonObject.getInt("id")
            val stationName = jsonObject.getString("stationName")

            val jsonSubObject: JSONObject = jsonObject.getJSONObject("city")
            val cityId: Int = jsonSubObject.getInt("id")
            val name: String = jsonSubObject.getString("name")

            val jsonSecondSubObject: JSONObject = jsonSubObject.getJSONObject("commune")
            val communeName: String = jsonSecondSubObject.getString("communeName")
            val districtName: String = jsonSecondSubObject.getString("districtName")
            val provinceName: String = jsonSecondSubObject.getString("provinceName")

            stations.add(Station(id, stationName, cityId, name, communeName, districtName, provinceName))

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
}