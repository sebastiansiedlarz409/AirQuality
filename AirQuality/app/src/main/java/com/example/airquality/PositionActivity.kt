package com.example.airquality

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.DaggerDependencies
import com.example.apiclient.APIClient
import com.example.database.DataBase
import com.example.database.PositionEntity
import com.example.service.DataManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_position.*
import kotlinx.android.synthetic.main.content_position.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PositionActivity : AppCompatActivity() {

    @Inject
    lateinit var apiClient: APIClient

    @Inject
    lateinit var dataManager: DataManager

    private lateinit var db: DataBase
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_position)
        DaggerDependencies.create().insertApiClientPositionActivity(this)

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

        var adapter: PositionAdapter
        val listItems: ArrayList<PositionEntity> = arrayListOf()

        CoroutineScope(Dispatchers.Default).launch {
            if(sharedPreferences.getLong("lastPositionRefreshTime${intent.getIntExtra("id", 0)}", 0) == 0.toLong()){
                refreshPosition()
                refreshLastUpdate()
            }
            else{
                val diff = Date().time - sharedPreferences.getLong("lastPositionRefreshTime${intent.getIntExtra("id", 0)}", 0)
                if(Date(diff).time / 60000 > 1440){
                    refreshPosition()
                    refreshLastUpdate()
                }
            }

            val positions = db.positionDao().getAllById(intent.getIntExtra("id", 0))

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

        positionsList.setOnItemClickListener{
                parent, _, position, _ ->
            val pos: PositionEntity = parent.getItemAtPosition(position) as PositionEntity
            val url = "https://www.google.com/search?q=" + pos.ParamName
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

    private suspend fun refreshPosition(){
        val refreshPositionTask: Deferred<Unit> = CoroutineScope(IO).async{
            dataManager.updatePositionData(this@PositionActivity, intent)
        }

        refreshPositionTask.await()
    }

    private fun refreshLastUpdate(){
        lastUpdatePosition.text = if (sharedPreferences.getLong("lastPositionRefreshTime${intent.getIntExtra("id", 0)}", 0) == 0.toLong())
            "Odświeżono: NIE"
        else
            "Odświeżono: ${SimpleDateFormat("dd/MM/yyyy HH:mm").format(sharedPreferences.getLong("lastPositionRefreshTime${intent.getIntExtra("id", 0)}", 0))}"
    }
}
