package com.example.airquality

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ListView
import com.example.apiclient.APIClient
import com.example.models.Station

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var list: ListView? = null
    var adapter: StationAdapter? = null
    private var search: EditText? = null
    private val apiClient: APIClient = APIClient(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "By Sebastian Siedlarz", Snackbar.LENGTH_LONG).show()
        }

        adapter = StationAdapter(this, arrayListOf())
        list = findViewById(R.id.stationList)
        search = findViewById(R.id.search)

        search?.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) { }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(search: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter?.filter(search)
            }
        })

        //onclick with popup
        list?.setOnItemClickListener{
                parent, _, position, _ ->
            val station: Station = parent.getItemAtPosition(position) as Station
            apiClient.getStationIndex(station.Id)
        }

        //Receiver list on screen
        apiClient.getAllStation()
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
