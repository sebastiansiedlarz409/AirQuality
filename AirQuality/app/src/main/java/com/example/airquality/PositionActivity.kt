package com.example.airquality

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.DaggerDependencies
import com.example.apiclient.APIClient
import com.example.models.Position

import kotlinx.android.synthetic.main.activity_position.*
import kotlinx.android.synthetic.main.activity_position.fab
import kotlinx.android.synthetic.main.content_position.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

class PositionActivity : AppCompatActivity() {

    @Inject
    lateinit var apiClient: APIClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_position)
        DaggerDependencies.create().insertApiClientPositionActivity(this)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "By Sebastian Siedlarz", Snackbar.LENGTH_LONG)
                .setAction("GITHUB", View.OnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sebastiansiedlarz409")))
                })
                .show()
        }

        var adapter = PositionAdapter(this, arrayListOf())
        val listItems: ArrayList<Position> = arrayListOf()

        CoroutineScope(IO).launch{
            for(item in apiClient.getPositionsData(apiClient.getPositions(intent.getIntExtra("id", 0).toInt()))){
                listItems.add(item)
            }

            withContext(Main){
                positionsList.adapter = adapter
            }
        }

        adapter = PositionAdapter(this, listItems)
        positionsList.adapter = adapter
    }
}
