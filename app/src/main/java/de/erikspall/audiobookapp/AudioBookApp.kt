package de.erikspall.audiobookapp

import android.app.Application
import com.google.android.material.color.DynamicColors

class AudioBookApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

    }
}