package de.erikspall.audiobookapp.domain.use_case.playback

import android.util.Log
import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository

class TogglePlayback (
    private val controllerRepo: PlayerControllerRepository
) {
    operator fun invoke() {
        if (controllerRepo.isPlaying()){
            Log.d("PlaybackUseCases", "pause!")
            controllerRepo.pause()
        } else {
            Log.d("PlaybackUseCases", "play!")
            controllerRepo.play()
        }
    }
}