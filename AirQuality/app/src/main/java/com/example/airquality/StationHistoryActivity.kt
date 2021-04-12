package com.example.airquality

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.database.DataBase
import com.example.database.StationHistoryEntity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_station_history.*

class StationHistoryActivity : AppCompatActivity() {

    private lateinit var db: DataBase
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_history)
        setSupportActionBar(findViewById(R.id.toolbar))

        sharedPreferences = getSharedPreferences("AiqQualitySP", Context.MODE_PRIVATE)

        db = DataBase.getDbInstance(this)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "By Sebastian Siedlarz", Snackbar.LENGTH_LONG)
                .setAction("GITHUB") {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sebastiansiedlarz409")))
                }
                .show()
        }

        var stationHistory : MutableList<StationHistoryEntity> = db.stationHistoryDao().getAll(intent.getIntExtra("id", 0))
        var adapter = StationHistoryAdapter(this, stationHistory)
        stationHistoryList.adapter = adapter
    }
}