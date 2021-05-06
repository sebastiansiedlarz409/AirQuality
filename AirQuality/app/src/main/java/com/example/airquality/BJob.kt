package com.example.airquality

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.apiclient.APIClient
import com.example.database.DataBase
import com.example.database.StationHistoryEntity
import com.example.database.StationIndexEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class BJob : JobService() {
    private lateinit var params: JobParameters
    private lateinit var apiClient: APIClient
    private lateinit var db: DataBase

    override fun onStartJob(params: JobParameters?): Boolean {
        this.params = params!!

        CoroutineScope(Dispatchers.Default).launch {
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            withContext(Main){
                Toast.makeText(applicationContext, "Update"+currentDate, Toast.LENGTH_SHORT).show()
            }

            db = DataBase.getDbInstance(applicationContext)
            apiClient = APIClient()

            val data: String? = apiClient.getAllStation()
            val stationsList: MutableList<StationIndexEntity> = apiClient.getAllStationList(data)

            db.stationIndexDao().deleteAll()

            for(item in stationsList){
                db.stationIndexDao().insert(item)
            }

            for(item in stationsList){
                db.stationHistoryDao().insert(
                    StationHistoryEntity(
                        item.StationId,
                        item.StationName,
                        item.Name,
                        item.Date,
                        item.Index,
                        Date().time
                    )
                )
            }

            applicationContext.getSharedPreferences("AiqQualitySP", Context.MODE_PRIVATE)
                .edit().putLong("lastStationRefreshTime", Date().time).apply()

            notifyJobFinished()
        }

        return true;
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    fun notifyJobFinished() {
        jobFinished(params,true)
    }
}