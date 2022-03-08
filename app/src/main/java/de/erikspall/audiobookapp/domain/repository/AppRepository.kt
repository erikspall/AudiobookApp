package de.erikspall.audiobookapp.domain.repository

import de.erikspall.audiobookapp.domain.model.AudiobookWithInfo
import de.erikspall.audiobookapp.domain.model.AudiobookWithPersons
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    suspend fun importAudiobooksFromLocalStorage()

    fun getAudiobooksWithPersons(): Flow<List<AudiobookWithPersons>>

    fun getAudiobooksWithInfo(): Flow<List<AudiobookWithInfo>>

    suspend fun setPosition(audiobookId: Long, position: Long, isPlaying: Boolean = false)

    suspend fun setChapterIsPlaying(audiobookId: Long, chapterId: Long, isPlaying: Boolean)
}