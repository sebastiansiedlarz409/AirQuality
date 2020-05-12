package com.example.airquality

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.DaggerDependencies
import com.example.apiclient.APIClient
import com.example.database.DataBase
import com.example.database.PositionEntity
import kotlinx.android.synthetic.main.activity_position.fab
import kotlinx.android.synthetic.main.content_position.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class PositionActivity : AppCompatActivity() {

    @Inject
    lateinit var apiClient: APIClient
    private lateinit var db: DataBase
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_position)
        DaggerDependencies.create().insertApiClientPositionActivity(this)

        sharedPreferences = getSharedPreferences("AiqQualitySP", Context.MODE_PRIVATE)

        db = Room.databaseBuilder(
            this,
            DataBase::class.java, "AirQualityDb"
        ).build()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "By Sebastian Siedlarz", Snackbar.LENGTH_LONG)
                .setAction("GITHUB") {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sebastiansiedlarz409")))
                }
                .show()
        }

        var adapter: PositionAdapter
        val listItems: ArrayList<PositionEntity> = arrayListOf()

        CoroutineScope(Dispatchers.Default).launch {
            if(sharedPreferences.getLong("lastPositionRefreshTime", 0) == 0.toLong()){
                refreshPosition()
            }
            else{
                val diff = Date().time - sharedPreferences.getLong("lastPositionRefreshTime", 0)
                if(Date(diff).time / 60000 > 1440){
                    refreshPosition()
                }
            }

            val positions = db.positionDao().getAll()

            for(item in positions){
                listItems.add(item)
            }

            withContext(Main){
                adapter = PositionAdapter(this@PositionActivity, listItems)
                positionsList.adapter = adapter
            }
        }

        adapter = PositionAdapter(this, listItems)
        positionsList.adapter = adapter
    }

    private suspend fun refreshPosition(){
        val refreshPositionDb: Deferred<Unit> = CoroutineScope(IO).async{
            val positions: MutableList<PositionEntity>
                    = apiClient.getPositionsData(apiClient.getPositions(intent.getIntExtra("id", 0)))

            db.positionDao().deleteAll()

            for(item in positions){
                db.positionDao().insert(item)
            }

            sharedPreferences.edit().putLong("lastPositionRefreshTime", Date().time).apply()
        }

        refreshPositionDb.await()
    }
}
