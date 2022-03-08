package de.erikspall.audiobookapp.domain.repository

import de.erikspall.audiobookapp.domain.model.Audiobook
import de.erikspall.audiobookapp.domain.model.AudiobookWithInfo
import de.erikspall.audiobookapp.domain.model.AudiobookWithPersons
import kotlinx.coroutines.flow.Flow

interface AudiobookRepository {
    fun getAudiobooks(): Flow<List<Audiobook>>

    fun getAudiobooksWithPersons(): Flow<List<AudiobookWithPersons>>

    fun getAudiobooksWithInfo(): Flow<List<AudiobookWithInfo>>

    suspend fun insert(audiobook: Audiobook): Long

    suspend fun insert(audiobooks: List<Audiobook>)

    suspend fun delete(audiobook: Audiobook)

    suspend fun getAudiobookById(id: Int): Audiobook?

    suspend fun getAudiobookByUri(uri: String): Audiobook?

    suspend fun getAudiobookWithInfoByUri(uri: String): AudiobookWithInfo?

    suspend fun audiobookExists(uri: String): Boolean

    suspend fun deleteAll()

    suspend fun setPosition(audiobookId: Long, position: Long, isPlaying: Boolean = false)
}