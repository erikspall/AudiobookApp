package de.erikspall.audiobookapp.domain.use_case.playback

import androidx.media3.common.C
import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository

class SeekTo(
    private val repository: PlayerControllerRepository
) {
    operator fun invoke(position: Long) {
        if (position <= repository.getCurrentChapterDuration() &&
                    position >= 0)
                        repository.seekTo(position)
    }

    fun chapter(chapterIndex: Int) {
        repository.seekTo(chapterIndex, C.TIME_UNSET)
    }
}