package de.erikspall.audiobookapp

import android.app.Application
import com.google.android.material.color.DynamicColors
import de.erikspall.audiobookapp.data.database.AudiobookRoomDatabase

class AudioBookApp : Application() {
    val database: AudiobookRoomDatabase by lazy { AudiobookRoomDatabase.getDatabase(this)}

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

    }


}