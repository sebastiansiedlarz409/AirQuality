package com.example.airquality

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.database.DataBase
import com.example.database.StationHistoryEntity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_station_history.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_station_history.*
import kotlinx.android.synthetic.main.content_station_history.lastUpdate
import kotlinx.android.synthetic.main.content_station_history.search
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class StationHistoryActivity : AppCompatActivity() {

    private lateinit var db: DataBase
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_history)

        sharedPreferences = getSharedPreferences("AiqQualitySP", Context.MODE_PRIVATE)

        db = DataBase.getDbInstance(this)

        lastUpdate.text = intent.getStringExtra("lastUpdate")
        progress.visibility = View.GONE

        fab.setOnClickListener { view ->
            Snackbar.make(view, "By Sebastian Siedlarz", Snackbar.LENGTH_LONG)
                .setAction("GITHUB") {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sebastiansiedlarz409")))
                }
                .show()
        }

        var stationHistory: MutableList<StationHistoryEntity> = arrayListOf()
        var adapter = StationHistoryAdapter(this@StationHistoryActivity, stationHistory)

        search.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) { }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(search: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter(search)
            }
        })

        CoroutineScope(Dispatchers.Default).launch {
            stationHistory = db.stationHistoryDao().getAll(intent.getIntExtra("id", 0))

            withContext(Dispatchers.Main){
                adapter = StationHistoryAdapter(this@StationHistoryActivity, stationHistory)
                stationHistoryList.adapter = adapter
            }
        }
    }
}