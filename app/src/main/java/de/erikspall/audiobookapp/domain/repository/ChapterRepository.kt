package de.erikspall.audiobookapp.domain.repository

import de.erikspall.audiobookapp.domain.model.Chapter
import kotlinx.coroutines.flow.Flow

interface ChapterRepository {

    suspend fun insert(chapter: Chapter): Long

    suspend fun update(chapter: Chapter)

    suspend fun delete(chapter: Chapter)

    fun getChapterById(id: Int): Flow<List<Chapter>>

    fun getChapters(): Flow<List<Chapter>>

    suspend fun deleteAll()

    suspend fun chapterExists(title: String, start: Double, start_time: String, end: Double, end_time: String): Boolean

    suspend fun getChaptersOfAudiobook(audiobookId: Long): List<Chapter>
}