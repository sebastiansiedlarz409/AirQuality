package com.example

import com.example.airquality.MainActivity
import com.example.airquality.PositionActivity
import dagger.Component

@Component
interface Dependencies {
    fun insertApiClientMainActivity(app: MainActivity)
    fun insertApiClientPositionActivity(app: PositionActivity)
    fun insertDataManagerMainActivity(app: MainActivity)
}