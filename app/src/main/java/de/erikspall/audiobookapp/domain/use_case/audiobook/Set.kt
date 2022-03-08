package de.erikspall.audiobookapp.domain.use_case.audiobook

import android.util.Log
import de.erikspall.audiobookapp.domain.repository.AppRepository

class Set(
    private val repository: AppRepository
) {
    /**
     * Saves position in audiobook table and also updates whether the audiobook and corresponding
     * chapter is playing or not (needed to display status in UI)
     */
    suspend fun position(audiobookId: Long, chapterId: Long, position: Long, isPlaying: Boolean = false) {
        Log.d("Saving", "Saving position: $position for book: $audiobookId")
        repository.setPosition(audiobookId, position, isPlaying)
        repository.setChapterIsPlaying(audiobookId, chapterId, isPlaying)
    }

    suspend fun chapterIsPlaying(audiobookId: Long, chapterId: Long, isPlaying: Boolean) {
        repository.setChapterIsPlaying(audiobookId, chapterId, isPlaying)
    }

    suspend fun bookIsPlaying(audiobookId: Long, isPlaying: Boolean) {
        repository.setBookIsPlaying(audiobookId, isPlaying)
    }

    suspend fun bothArePlaying(audiobookId: Long, chapterId: Long, isPlaying: Boolean) {
        chapterIsPlaying(audiobookId, chapterId, isPlaying)
        bookIsPlaying(audiobookId, isPlaying)
    }
}