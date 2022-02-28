package de.erikspall.audiobookapp.domain.repository

import de.erikspall.audiobookapp.domain.model.Audiobook
import de.erikspall.audiobookapp.domain.model.AudiobookWithGenres
import de.erikspall.audiobookapp.domain.model.BelongsTo
import de.erikspall.audiobookapp.domain.model.GenreWithAudiobooks
import kotlinx.coroutines.flow.Flow

interface BelongsToRepository {

    suspend fun insert(belongsTo: BelongsTo): Long

    suspend fun update(belongsTo: BelongsTo)

    suspend fun delete(belongsTo: BelongsTo)

    fun getGenreWithAudiobooks(): Flow<List<GenreWithAudiobooks>>

    fun getAudiobookWithGenres(): Flow<List<AudiobookWithGenres>>

    fun getAllAudiobooksOfGenre(genreId: Long): Flow<List<Audiobook>>

    suspend fun addGenreToAudiobook(genreId: Long?, audiobookId: Long?)

    suspend fun deleteAll()
}