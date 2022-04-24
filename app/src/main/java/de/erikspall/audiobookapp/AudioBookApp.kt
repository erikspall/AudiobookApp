package de.erikspall.audiobookapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AudioBookApp : Application() {
    //val database: AudiobookDatabase by lazy { AudiobookDatabase.getDatabase(this)}
    /*private val sleepTimerPref = getSharedPreferences(getString(R.string.sleep_timer_shared_pref_name), Context.MODE_PRIVATE)

    fun getSleepTimerPref(): SharedPreferences {
        return sleepTimerPref
    }*/

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

}