package com.example.airquality

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.DaggerDependencies
import com.example.apiclient.APIClient
import com.example.database.DataBase
import com.example.database.StationHistoryEntity
import com.example.database.StationIndexEntity
import com.example.location.Location
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.info_popup.*
import kotlinx.android.synthetic.main.info_popup.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.security.Permission
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var apiClient: APIClient
    private lateinit var db: DataBase
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DaggerDependencies.create().insertApiClientMainActivity(this)

        ActivityCompat.requestPermissions(this@MainActivity, Array<String>(1){android.Manifest.permission.ACCESS_FINE_LOCATION}, 101)

        sharedPreferences = getSharedPreferences("AiqQualitySP", Context.MODE_PRIVATE)

        db = DataBase.getDbInstance(this)

        refreshLastUpdate()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "By Sebastian Siedlarz", Snackbar.LENGTH_LONG)
                .setAction("GITHUB") {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sebastiansiedlarz409")))
                }
                .show()
        }

        progress.visibility = View.GONE

        var adapter = StationAdapter(this, arrayListOf())

        search.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) { }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(search: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter(search)
            }
        })

        //onclick with popup
        stationList.setOnItemClickListener{
                parent, _, position, _ ->

            val station: StationIndexEntity = parent.getItemAtPosition(position) as StationIndexEntity

            val intentPosition = Intent(this, PositionActivity::class.java)
            intentPosition.putExtra("id", station.StationId)

            val urlMaps: Uri = Uri.parse("geo:0,0?q=${station.Lat},${station.Lon}(Czujnik)")
            val intentMaps = Intent(Intent.ACTION_VIEW, urlMaps)

            val intentHistory = Intent(this, StationHistoryActivity::class.java)
            intentHistory.putExtra("id", station.StationId)
            intentHistory.putExtra("lastUpdate", lastUpdate.text)

            val inflater = layoutInflater
            val popup = inflater.inflate(R.layout.info_popup, findViewById(R.id.root))

            popup.date.text = "Data: " + station.Date
            popup.index.text = "Indeks: " + station.Index

            val builder = AlertDialog.Builder(this)

            builder.setView(popup);
            var dialog = builder.show();

            popup.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

            popup.map.setOnClickListener {
                dialog.dismiss()
                startActivity(intentMaps)
            }
            popup.sensors.setOnClickListener {
                dialog.dismiss()
                startActivity(intentPosition)
            }
            popup.history.setOnClickListener {
                dialog.dismiss()
                startActivity(intentHistory)
            }
            popup.ok.setOnClickListener {
                dialog.dismiss()
            }

            val index: String? = station.Index

            when {
                index.equals("Bardzo dobry") -> {
                    popup.pop.setBackgroundResource(R.drawable.card_background_vg)
                }
                index.equals("Dobry") -> {
                    popup.pop.setBackgroundResource(R.drawable.card_background_g)
                }
                index.equals("Umiarkowany") -> {
                    popup.pop.setBackgroundResource(R.drawable.card_background_u)
                }
                index.equals("Dostateczny") -> {
                    popup.pop.setBackgroundResource(R.drawable.card_background_c)
                }
                index.equals("Zły") -> {
                    popup.pop.setBackgroundResource(R.drawable.card_background_b)
                }
                index.equals("Bardzo zły") -> {
                    popup.pop.setBackgroundResource(R.drawable.card_background_vb)
                }
            }
        }

        //Receiver list on screen
        val listItems: ArrayList<StationIndexEntity> = arrayListOf()

        CoroutineScope(Dispatchers.Default).launch {
            if(sharedPreferences.getLong("lastStationRefreshTime", 0) == 0.toLong()){
                refreshStationIndex()
                refreshLastUpdate()
            }
            else{
                val diff = Date().time - sharedPreferences.getLong("lastStationRefreshTime", 0)
                if(Date(diff).time / 60000 > 60){
                    refreshStationIndex()
                    refreshLastUpdate()
                }
            }

            val stations = db.stationIndexDao().getAll()
            val location: Location = Location()
            var nearest: StationIndexEntity? = null

            if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){

            }
            else{
                nearest = location.getNearestStation(stations, this@MainActivity)
            }

            for(item in stations){
                if(item.StationId.equals(nearest?.StationId))
                    continue
                listItems.add(item)
            }
            listItems.sort()
            if (nearest != null){
                listItems.add(0, nearest)
            }

            withContext(Main){
                adapter = StationAdapter(this@MainActivity, listItems)
                stationList.adapter = adapter
            }
        }

        adapter = StationAdapter(this, listItems)
        stationList.adapter = adapter
    }

    private suspend fun refreshStationIndex() {
        val refreshStationDb: Deferred<Unit> = CoroutineScope(IO).async{
            withContext(Main){
                progress.visibility = View.VISIBLE
            }

            val data: String? = apiClient.getAllStation()
            val stationsList: MutableList<StationIndexEntity> = apiClient.getAllStationList(data)

            db.stationIndexDao().deleteAll()

            for(item in stationsList){
                db.stationIndexDao().insert(item)
            }

            for(item in stationsList){
                db.stationHistoryDao().insert(StationHistoryEntity(item.StationId, item.StationName, item.Name, item.Date, item.Index, Date().time))
            }

            sharedPreferences.edit().putLong("lastStationRefreshTime", Date().time).apply()
        }

        refreshStationDb.await()

        withContext(Main){
            progress.visibility = View.GONE
        }
    }

    private fun refreshLastUpdate(){
        lastUpdate.text = if (sharedPreferences.getLong("lastStationRefreshTime", 0) == 0.toLong())
            "Odświeżono: NIE"
        else
            "Odświeżono: ${SimpleDateFormat("dd/MM/yyyy HH:mm").format(sharedPreferences.getLong("lastStationRefreshTime", 0))}"
    }
}
