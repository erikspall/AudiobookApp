package de.erikspall.audiobookapp.domain.use_case.playback

import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository

class Skip(
    private val repository: PlayerControllerRepository
) {
    fun forward() {
        repository.getController()?.seekToNextMediaItem()
    }

    fun backward() {
        repository.getController()?.seekToPrevious()
    }

    fun forward(ms: Long) {
        val newPosition = repository.getCurrentPositionInChapter() + ms
        if (newPosition <= repository.getCurrentChapterDuration())
            repository.getController()?.seekTo(newPosition)
        else
            forward()
    }

    fun backward(ms: Long) {
        val newPosition = repository.getCurrentPositionInChapter() - ms
        if (newPosition >= 0)
            repository.getController()?.seekTo(newPosition)
        else
            backward()
    }
}