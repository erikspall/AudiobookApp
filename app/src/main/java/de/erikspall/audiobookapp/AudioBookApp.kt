package de.erikspall.audiobookapp

import android.app.Application
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AudioBookApp : Application() {
    //val database: AudiobookDatabase by lazy { AudiobookDatabase.getDatabase(this)}

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

}