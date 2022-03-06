package de.erikspall.audiobookapp.domain.use_case.playback

import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository

class SeekTo(
    private val repository: PlayerControllerRepository
) {
    operator fun invoke(position: Long) {
        if (position <= repository.getCurrentChapterDuration() &&
                    position >= 0)
                        repository.seekTo(position)
    }
}