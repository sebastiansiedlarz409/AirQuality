package com.example.apiclient

import com.example.database.PositionEntity
import com.example.database.StationIndexEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import ru.gildor.coroutines.okhttp.await
import java.lang.Exception
import javax.inject.Inject

class APIClient @Inject constructor(){

    private val urlStation: String = "https://api.gios.gov.pl/pjp-api/rest/station/findAll"
    private val urlStationIndex: String = "https://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/"
    private val urlPositions: String = "https://api.gios.gov.pl/pjp-api/rest/station/sensors/"
    private val client = OkHttpClient.Builder().build()

    suspend fun getAllStation() : String? {

        val request = Request.Builder()
            .url(this.urlStation)
            .build()

        val response: Response = client.newCall(request).await()

        return withContext(IO) { response.body()?.string() }

    }

    suspend fun getAllStationList(data: String?) : MutableList<StationIndexEntity> {

        val jsonArray = JSONArray(data)
        val count: Int = jsonArray.length()
        val stations: MutableList<StationIndexEntity> = mutableListOf()

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

            stations.add(StationIndexEntity(id, stationName, cityId, name, communeName, districtName, provinceName, lat, lon, "", ""))
        }

        val jobs: MutableList<Deferred<Unit>> = mutableListOf()
        for(item in stations){
            val job = CoroutineScope(IO).async {
                val temp: Pair<String, String> = getStationIndexData(getStationIndex(item))
                item.Date = temp.first
                item.Index = temp.second
            }
            jobs.add(job)
        }

        jobs.awaitAll()

        return stations
    }

    private suspend fun getStationIndex(station: StationIndexEntity): String? {

        client.dispatcher().maxRequests = 90
        client.dispatcher().maxRequestsPerHost = 90

        val request = Request.Builder()
            .url(this.urlStationIndex + station.StationId)
            .build()

        val response: Response = client.newCall(request).await()

        return withContext(IO) { response.body()?.string() }

    }

    private fun getStationIndexData(data: String?): Pair<String, String> {
        try{
            val jsonObject = JSONObject(data)
            val date = jsonObject.getString("stCalcDate")
            val jsonSubObject: JSONObject = jsonObject.getJSONObject("stIndexLevel")
            val index = jsonSubObject.getString("indexLevelName")
            return Pair(date,index)
        }
        catch (ex: Exception) {}
        return Pair("Brak danych","Brak indexu")
    }

    suspend fun getPositions(index: Int): String? {

        if(index == 0){
            return null
        }

        val request = Request.Builder()
            .url(this.urlPositions + index)
            .build()

        val response: Response = client.newCall(request).await()

        return withContext(IO) { response.body()?.string() }
    }

    fun getPositionsData(data: String?) : MutableList<PositionEntity> {

        val positions: MutableList<PositionEntity> = mutableListOf()

        if(data.isNullOrEmpty()){
            return positions
        }

        val jsonArray = JSONArray(data)
        val count: Int = jsonArray.length()

        for(i in 1..count){
            val jsonObject = jsonArray.getJSONObject(i-1)
            val stationId = jsonObject.getInt("stationId")
            val jsonSubObject: JSONObject = jsonObject.getJSONObject("param")
            val paramName = jsonSubObject.getString("paramName")
            val paramFormula = jsonSubObject.getString("paramFormula")
            val paramCode = jsonSubObject.getString("paramCode")

            positions.add(PositionEntity(stationId, paramName, paramFormula, paramCode))
        }

        return positions
    }
}