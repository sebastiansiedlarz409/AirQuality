package com.example.airquality

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.apiclient.APIClient
import com.example.models.Station

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val apiClient: APIClient = APIClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "By Sebastian Siedlarz", Snackbar.LENGTH_LONG).show()
        }

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

            val station: Station = parent.getItemAtPosition(position) as Station

            val intentPosition = Intent(this, PositionActivity::class.java)
            intentPosition.putExtra("id", station.Id)

            val urlMaps: Uri = Uri.parse("geo:0,0?q=${station.Lat},${station.Lon}(Czujnik)")
            val intentMaps = Intent(Intent.ACTION_VIEW, urlMaps)

            val builder = AlertDialog.Builder(this, R.style.AirAlert)

            builder.setTitle("Jakość powietrza")
            builder.setMessage("Data: ${station.Index?.Date} \nIndeks: ${station.Index?.Index}")
            builder.setIcon(R.drawable.ic_info_outline_black_24dp)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.setNeutralButton("Czujniki") { _, _ ->
                startActivity(intentPosition)
            }
            builder.setNegativeButton("Mapa") { _, _ ->
                startActivity(intentMaps)
            }
            builder.show()
        }

        //Receiver list on screen
        val listItems: ArrayList<Station> = arrayListOf()

        CoroutineScope(IO).launch{
            val data: String? = apiClient.getAllStation()
            val stationsList: MutableList<Station> = apiClient.getAllStationList(data)

            for(item in stationsList){
                listItems.add(item)
            }

            withContext(Main){
                listItems.sort()
                stationList.adapter = adapter
            }
        }

        adapter = StationAdapter(this, listItems)
        stationList.adapter = adapter
    }
}
