package com.example.airquality

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.apiclient.APIClient
import com.example.models.Position
import com.example.models.Station
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.activity_position.*
import kotlinx.android.synthetic.main.activity_position.fab
import kotlinx.android.synthetic.main.activity_position.toolbar
import kotlinx.android.synthetic.main.content_position.*
import kotlinx.coroutines.*

class PositionActivity : AppCompatActivity() {

    private val apiClient: APIClient = APIClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_position)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "By Sebastian Siedlarz", Snackbar.LENGTH_LONG).show()
        }

        var adapter = PositionAdapter(this, arrayListOf())
        val listItems: ArrayList<Position> = arrayListOf()

        val positons: Deferred<String?> = GlobalScope.async { apiClient.getPositions(intent.getIntExtra("id", 0).toInt()) }
        GlobalScope.launch{
            for(item in apiClient.getPositionsData(positons.await())){
                listItems.add(item)
            }
            runOnUiThread{
                positionsList.adapter = adapter
            }
        }

        adapter = PositionAdapter(this, listItems)
        positionsList.adapter = adapter
    }

}
