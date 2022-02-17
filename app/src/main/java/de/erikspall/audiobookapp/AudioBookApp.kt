package de.erikspall.audiobookapp

import android.app.Application
import com.google.android.material.color.DynamicColors
import de.erikspall.audiobookapp.data.database.AudiobookRoomDatabase
import de.erikspall.audiobookapp.data.repository.DatabaseRepository

class AudioBookApp : Application() {
    val database: AudiobookRoomDatabase by lazy { AudiobookRoomDatabase.getDatabase(this)}
    val repository by lazy {
        DatabaseRepository(
            database.audiobookDao(),
            database.belongsToDao(),
            database.chapterDao(),
            database.genreDao(),
            database.personDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}