package com.example.service

import android.content.Context
import android.content.Intent
import com.example.apiclient.APIClient
import com.example.database.DataBase
import com.example.database.PositionEntity
import com.example.database.StationHistoryEntity
import com.example.database.StationIndexEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.*
import javax.inject.Inject

class DataManager @Inject constructor(){
    private lateinit var apiClient: APIClient
    private lateinit var db: DataBase

    suspend fun updateStationData(context: Context){
        val task: Deferred<Unit> = CoroutineScope(Dispatchers.IO).async {
            db = DataBase.getDbInstance(context)
            apiClient = APIClient()

            val data: String? = apiClient.getAllStation()
            val stationsList: MutableList<StationIndexEntity> = apiClient.getAllStationList(data)

            db.stationIndexDao().deleteAll()

            for(item in stationsList){
                db.stationIndexDao().insert(item)
            }

            for(item in stationsList){
                db.stationHistoryDao().insert(
                    StationHistoryEntity(
                        item.StationId,
                        item.StationName,
                        item.Name,
                        item.Date,
                        item.Index,
                        Date().time
                    )
                )
            }

            context.getSharedPreferences("AiqQualitySP", Context.MODE_PRIVATE)
                .edit().putLong("lastStationRefreshTime", Date().time).apply()
        }

        task.await()
    }

    suspend fun updatePositionData(context: Context, intent: Intent){
        val task: Deferred<Unit> = CoroutineScope(Dispatchers.IO).async {
            db = DataBase.getDbInstance(context)
            apiClient = APIClient()

            val positions: MutableList<PositionEntity>
                    = apiClient.getPositionsData(apiClient.getPositions(intent.getIntExtra("id", 0)))

            db.positionDao().deleteAll(intent.getIntExtra("id", 0))

            for(item in positions){
                db.positionDao().insert(item)
            }

            context.getSharedPreferences("AiqQualitySP", Context.MODE_PRIVATE)
                .edit().putLong("lastPositionRefreshTime${intent.getIntExtra("id", 0)}", Date().time).apply()
        }

        task.await()
    }
}