package de.erikspall.audiobookapp.domain.use_case.playback

import android.util.Log
import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository
import de.erikspall.audiobookapp.domain.services.playback.background.listeners.PlayerListener

class AddListener(
    private val repository: PlayerControllerRepository
) {
    private var previousListener: PlayerListener? = null

    operator fun invoke(listener: PlayerListener, removePreviousListeners: Boolean = false){
        if (removePreviousListeners && previousListener != null){
            repository.getController()?.removeListener(previousListener!!)
            Log.d("PlaybackUseCases", "Previous listener removed")
        }

        repository.getController()?.addListener(listener)
        previousListener = listener
    }
}