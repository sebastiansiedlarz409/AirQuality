package com.example.airquality

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.example.apiclient.APIClient
import com.example.models.Station

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val apiClient: APIClient = APIClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

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
            val index: Deferred<String?> = GlobalScope.async { apiClient.getStationIndex(station.Id) }
            val builder = AlertDialog.Builder(this)
            Log.d("asd", "ASd")
            GlobalScope.launch {
                val stationIndex = apiClient.getStationIndexData(index.await())
                builder.setTitle("Data: " + stationIndex.Date)
                builder.setMessage("Indeks jakości: " + stationIndex.Index)

                runOnUiThread{
                    builder.setIcon(R.drawable.ic_info_outline_black_24dp)
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            }
        }

        //Receiver list on screen

        val listItems: ArrayList<Station> = arrayListOf()
        val stations: Deferred<String?> = GlobalScope.async { apiClient.getAllStation() }
        GlobalScope.launch{
            for(item in apiClient.getAllStationList(stations.await())){
                listItems.add(item)
            }
            runOnUiThread{
                listItems.sort()
                stationList.adapter = adapter
            }
        }

        adapter = StationAdapter(this, listItems)
        stationList.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
