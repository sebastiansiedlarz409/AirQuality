package com.example.airquality

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
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

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "AirQualityNotification"
    private val description = "AirQuality notification channel"

    override fun onStartJob(params: JobParameters?): Boolean {
        this.params = params!!

        dataManager = DataManager()
        location = Location(applicationContext, false)
        db = DataBase.getDbInstance(applicationContext)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

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
                if(nearest.Index == "Umiarkowany" || nearest.Index == "Dostateczny" || nearest.Index == "Zły"
                    || nearest.Index == "Bardzo zły" || nearest.Index == "Brak indeksu"){
                    withContext(Main){
                        builder = Notification.Builder(applicationContext, channelId)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setSmallIcon(R.drawable.ic_baseline_wb_cloudy_24)
                            .setContentIntent(pendingIntent)
                            .setContentTitle("AirQuality")
                            .setContentText("Uwaga!!! Indeks " + nearest.Index + " dla " + nearest.Name + "!!!")
                        with(NotificationManagerCompat.from(applicationContext)) {
                            notify(123, builder.build())
                        }
                    }
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