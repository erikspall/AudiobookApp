package de.erikspall.audiobookapp.data.repository

import de.erikspall.audiobookapp.data.data_source.local.database.dao.GenreDao
import de.erikspall.audiobookapp.domain.model.Genre
import de.erikspall.audiobookapp.domain.repository.GenreRepository
import kotlinx.coroutines.flow.Flow

class GenreRepositoryImpl(
    private val genreDao: GenreDao
) : GenreRepository {
    override suspend fun insert(genre: Genre): Long {
        return genreDao.insert(genre)
    }

    override suspend fun update(genre: Genre) {
        genreDao.update(genre)
    }

    override suspend fun genreExists(genre: String): Boolean {
        return genreDao.genreExists(genre)
    }

    override suspend fun delete(genre: Genre) {
        genreDao.delete(genre)
    }

    override suspend fun getGenre(name: String): Genre? {
        return genreDao.getGenre(name)
    }

    override suspend fun deleteAll() {
        genreDao.deleteAll()
    }

    override fun getGenresOf(audiobookId: Long): Flow<List<Genre>> {
        return genreDao.getGenresOf(audiobookId)
    }
}