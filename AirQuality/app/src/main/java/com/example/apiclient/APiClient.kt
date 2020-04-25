package com.example.apiclient

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.util.Log
import com.example.airquality.MainActivity
import com.example.airquality.R
import com.example.airquality.StationAdapter
import com.example.models.Station
import com.example.models.StationIndex
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class APIClient(context: MainActivity){

    private val urlStation: String = "https://api.gios.gov.pl/pjp-api/rest/station/findAll"
    private val urlStationIndex: String = "https://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/"
    private val client = OkHttpClient()
    private var context: MainActivity = context

    fun getAllStation() {

        val listItems: ArrayList<Station> = arrayListOf<Station>()

        val request = Request.Builder()
            .url(this.urlStation)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response){

                for(item in getAllStationList(response.body()?.string())){
                    listItems.add(item)
                }

                listItems.sort()

                context.adapter = StationAdapter(context, listItems)

                context.runOnUiThread(Runnable { context.list?.adapter = context.adapter  })
            }
        })
    }

    fun getAllStationList(data: String?) : MutableList<Station> {

        val jsonArray: JSONArray = JSONArray(data)
        val count: Int = jsonArray.length()
        val stations: MutableList<Station> = mutableListOf<Station>()

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

    fun getStationIndex(index: Int) {

        val request = Request.Builder()
            .url(this.urlStationIndex + index)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response){

                var stationIndex: StationIndex = getStationIndexData(response.body()?.string())

                context.runOnUiThread(Runnable {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Data: " + stationIndex.Date)
                    builder.setMessage("Indeks jakości: " + stationIndex.Index)
                    builder.setIcon(R.drawable.ic_info_outline_black_24dp)
                    builder.setPositiveButton("OK", DialogInterface.OnClickListener(){
                        dialog, w ->
                        dialog.dismiss()
                    })
                    builder.show()
                })
            }
        })

    }

    fun getStationIndexData(data: String?) : StationIndex {

        val jsonObject: JSONObject = JSONObject(data)
        val id = jsonObject.getInt("id")
        val date = jsonObject.getString("stCalcDate")
        val jsonSubObject: JSONObject = jsonObject.getJSONObject("stIndexLevel")
        val index = jsonSubObject.getString("indexLevelName")

        return StationIndex(id, date, index)
    }
}