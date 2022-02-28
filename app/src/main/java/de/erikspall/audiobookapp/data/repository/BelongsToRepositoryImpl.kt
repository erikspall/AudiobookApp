package de.erikspall.audiobookapp.data.repository

import de.erikspall.audiobookapp.data.data_source.local.database.dao.BelongsToDao
import de.erikspall.audiobookapp.domain.model.Audiobook
import de.erikspall.audiobookapp.domain.model.AudiobookWithGenres
import de.erikspall.audiobookapp.domain.model.BelongsTo
import de.erikspall.audiobookapp.domain.model.GenreWithAudiobooks
import de.erikspall.audiobookapp.domain.repository.BelongsToRepository
import kotlinx.coroutines.flow.Flow

class BelongsToRepositoryImpl(
    private val belongsToDao: BelongsToDao
) : BelongsToRepository {
    override suspend fun insert(belongsTo: BelongsTo): Long {
        return belongsToDao.insert(belongsTo)
    }

    override suspend fun update(belongsTo: BelongsTo) {
        belongsToDao.update(belongsTo)
    }

    override suspend fun delete(belongsTo: BelongsTo) {
        belongsToDao.delete(belongsTo)
    }

    override fun getGenreWithAudiobooks(): Flow<List<GenreWithAudiobooks>> {
        return belongsToDao.getGenreWithAudiobooks()
    }

    override fun getAudiobookWithGenres(): Flow<List<AudiobookWithGenres>> {
        return belongsToDao.getAudiobookWithGenres()
    }

    override fun getAllAudiobooksOfGenre(genreId: Long): Flow<List<Audiobook>> {
        return belongsToDao.getAllAudiobooksOfGenre(genreId)
    }

    override suspend fun addGenreToAudiobook(genreId: Long?, audiobookId: Long?) {
        belongsToDao.addGenreToAudiobook(genreId, audiobookId)
    }

    override suspend fun deleteAll() {
        belongsToDao.deleteAll()
    }
}