package com.example.airquality

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.database.DataBase
import com.example.database.StationIndexEntity
import com.example.location.Location
import com.example.service.DataManager
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.text.SimpleDateFormat
import java.util.*

class BJob : JobService() {
    private lateinit var params: JobParameters
    private lateinit var dataManager: DataManager
    private lateinit var db: DataBase
    private lateinit var location: Location
    private lateinit var job: Job

    override fun onStartJob(params: JobParameters?): Boolean {
        this.params = params!!

        dataManager = DataManager()
        location = Location(applicationContext, false)
        db = DataBase.getDbInstance(applicationContext)

        job = CoroutineScope(Dispatchers.Default).launch {
            dataManager.updateStationData(applicationContext)

            val stations = db.stationIndexDao().getAll()

            var nearest: StationIndexEntity? = null

            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ){
                //DO NOTHING
            }
            else{
                nearest = location.getNearestStation(stations, applicationContext)
            }

            if(nearest != null){
                withContext(Main){
                    var builder = NotificationCompat.Builder(applicationContext, "123")
                        .setSmallIcon(R.drawable.ic_baseline_wb_cloudy_24)
                        .setContentTitle("AirQuality")
                        .setContentText("Indeks " + nearest.Index + " dla " + nearest.Name)
                        .setStyle(NotificationCompat.BigTextStyle()
                            .bigText("Much longer text that cannot fit one line..."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    with(NotificationManagerCompat.from(applicationContext)) {
                        notify(123, builder.build())
                    }
                }
            }
            else{
                var builder = NotificationCompat.Builder(applicationContext, "123")
                    .setSmallIcon(R.drawable.ic_baseline_wb_cloudy_24)
                    .setContentTitle("AirQuality")
                    .setContentText("Indeks asd dla ")
                    .setStyle(NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                with(NotificationManagerCompat.from(applicationContext)) {
                    notify(123, builder.build())
                }
            }

            notifyJobFinished()
        }

        return true;
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        job.cancel()
        return false
    }

    private fun notifyJobFinished() {
        jobFinished(params,true)
    }
}