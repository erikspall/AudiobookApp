package de.erikspall.audiobookapp.data.repository.database

import de.erikspall.audiobookapp.data.source.local.database.dao.AudiobookDao
import de.erikspall.audiobookapp.domain.model.Audiobook
import de.erikspall.audiobookapp.domain.model.AudiobookWithInfo
import de.erikspall.audiobookapp.domain.model.AudiobookWithPersons
import de.erikspall.audiobookapp.domain.repository.AudiobookRepository
import kotlinx.coroutines.flow.Flow

class AudiobookRepositoryImpl(
    private val audiobookDao: AudiobookDao
) : AudiobookRepository {
    override fun getAudiobooks(): Flow<List<Audiobook>> {
        return audiobookDao.getAudiobooks()
    }

    override fun getAudiobooksWithPersons(): Flow<List<AudiobookWithPersons>> {
        return audiobookDao.getAudiobooksWithPersons()
    }

    override fun getAudiobooksWithInfo(): Flow<List<AudiobookWithInfo>> {
        return audiobookDao.getAudiobooksWithInfo()
    }

    override suspend fun insert(audiobook: Audiobook): Long {
        return audiobookDao.insert(audiobook)
    }

    override suspend fun insert(audiobooks: List<Audiobook>) {
        return audiobookDao.insert(audiobooks)
    }

    override suspend fun delete(audiobook: Audiobook) {
        audiobookDao.delete(audiobook)
    }

    override suspend fun getAudiobookById(id: Int): Audiobook? {
        return audiobookDao.getAudiobookById(id)
    }

    override suspend fun getAudiobookByUri(uri: String): Audiobook? {
        return audiobookDao.getAudiobookByUri(uri)
    }

    override suspend fun getAudiobookWithInfoByUri(uri: String): AudiobookWithInfo? {
        return audiobookDao.getAudiobookWithInfoByUri(uri)
    }

    override suspend fun audiobookExists(uri: String): Boolean {
        return audiobookDao.audiobookExists(uri)
    }

    override suspend fun deleteAll() {
        audiobookDao.deleteAll()
    }

    override suspend fun setPosition(audiobookId: Long, position: Long, isPlaying: Boolean) {
        audiobookDao.setPosition(audiobookId, position, isPlaying)
    }

    override suspend fun setBookIsPlaying(audiobookId: Long, isPlaying: Boolean) {
        audiobookDao.setBookIsPlaying(audiobookId, isPlaying)
    }


}