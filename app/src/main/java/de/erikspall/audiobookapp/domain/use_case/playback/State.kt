package de.erikspall.audiobookapp.domain.use_case.playback

import androidx.media3.common.Player
import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository

class State(
    private val repository: PlayerControllerRepository
) {
    fun isPlaying(): Boolean {
        return repository.isPlaying()
    }

    fun isPrepared(): Boolean {
        return when (repository.getController()?.playbackState) {
            Player.STATE_READY -> true
            Player.STATE_IDLE -> false
            Player.STATE_ENDED -> true
            Player.STATE_BUFFERING -> true
            else -> false
        }
    }
}