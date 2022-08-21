package de.erikspall.audiobookapp.domain.services.playback

import android.content.Context
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import de.erikspall.audiobookapp.R

class CastOptionsProvider: OptionsProvider {
    override fun getCastOptions(p0: Context): CastOptions {
        return CastOptions.Builder()
            .setReceiverApplicationId("4F8B3483")
            .build()
    }

    override fun getAdditionalSessionProviders(p0: Context): MutableList<SessionProvider>? {
        return mutableListOf()
    }
}