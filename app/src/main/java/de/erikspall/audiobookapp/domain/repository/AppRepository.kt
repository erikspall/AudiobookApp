package de.erikspall.audiobookapp.domain.repository

import de.erikspall.audiobookapp.domain.model.AudiobookWithPersons
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    suspend fun importAudiobooksFromLocalStorage()

    fun getAudiobooksWithPersons(): Flow<List<AudiobookWithPersons>>
}