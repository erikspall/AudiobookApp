package de.erikspall.audiobookapp.data.repository

import de.erikspall.audiobookapp.data.data_source.local.database.dao.ChapterDao
import de.erikspall.audiobookapp.domain.model.Chapter
import de.erikspall.audiobookapp.domain.repository.ChapterRepository
import kotlinx.coroutines.flow.Flow

class ChapterRepositoryImpl(
    private val chapterDao: ChapterDao
) : ChapterRepository {
    override suspend fun insert(chapter: Chapter): Long {
        return chapterDao.insert(chapter)
    }

    override suspend fun update(chapter: Chapter) {
        chapterDao.update(chapter)
    }

    override suspend fun delete(chapter: Chapter) {
        chapterDao.delete(chapter)
    }

    override fun getChapterById(id: Int): Flow<List<Chapter>> {
        return chapterDao.getChapterById(id)
    }

    override fun getChapters(): Flow<List<Chapter>> {
        return chapterDao.getChapters()
    }

    override suspend fun deleteAll() {
        chapterDao.deleteAll()
    }

    override suspend fun chapterExists(
        title: String,
        start: Double,
        start_time: String,
        end: Double,
        end_time: String
    ): Boolean {
        return chapterDao.chapterExists(title, start, start_time, end, end_time)
    }

    override suspend fun getChaptersOfAudiobook(audiobookId: Long): List<Chapter> {
        return chapterDao.getChaptersOfAudiobook(audiobookId)
    }
}