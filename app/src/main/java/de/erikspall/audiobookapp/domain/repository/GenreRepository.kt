package de.erikspall.audiobookapp.domain.repository

import de.erikspall.audiobookapp.domain.model.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {

    suspend fun insert(genre: Genre): Long

    suspend fun update(genre: Genre)

    suspend fun genreExists(genre: String): Boolean

    suspend fun delete(genre: Genre)

    suspend fun getGenre(name: String): Genre?

    suspend fun deleteAll()

    fun getGenresOf(audiobookId: Long): Flow<List<Genre>>

}