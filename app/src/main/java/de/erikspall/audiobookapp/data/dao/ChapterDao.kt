package de.erikspall.audiobookapp.data.dao

import androidx.room.*
import de.erikspall.audiobookapp.data.model.Audiobook
import de.erikspall.audiobookapp.data.model.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(chapter: Chapter)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSync(chapter: Chapter): Long

    @Update
    suspend fun update(chapter: Chapter)

    @Delete
    suspend fun delete(chapter: Chapter)

    @Query("SELECT * FROM chapter WHERE chapterId = :id")
    fun getItem(id: Int): Flow<List<Chapter>>

    @Query("SELECT * FROM chapter")
    fun getItems(): Flow<List<Chapter>>

    @Query("SELECT * FROM chapter WHERE title = :title AND start = :start AND start_time = :start_time AND end = :end AND end_time = :end_time COLLATE NOCASE LIMIT 1")
    fun getChapterSync(title: String, start: Double, start_time: String, end: Double, end_time: String): Chapter

    @Query("DELETE FROM chapter")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM chapter WHERE title = :title AND start = :start AND start_time = :start_time AND end = :end AND end_time = :end_time COLLATE NOCASE)")
    fun chapterExistsSync(title: String, start: Double, start_time: String, end: Double, end_time: String): Boolean

    @Query("SELECT * FROM chapter WHERE chapter.audiobookId = :audiobookId")
    suspend fun getChaptersOfAudiobook(audiobookId: Long): List<Chapter>
}