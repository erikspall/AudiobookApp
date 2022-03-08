package de.erikspall.audiobookapp.data.data_source.local.database.dao

import androidx.room.*
import de.erikspall.audiobookapp.domain.model.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(chapter: Chapter): Long

    @Update
    suspend fun update(chapter: Chapter)

    @Delete
    suspend fun delete(chapter: Chapter)

   @Query("SELECT * FROM chapter WHERE chapterId = :id")
    fun getChapterById(id: Int): Flow<List<Chapter>>

    @Query("SELECT * FROM chapter")
    fun getChapters(): Flow<List<Chapter>>

    @Query("DELETE FROM chapter")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM chapter WHERE title = :title AND start = :start AND start_time = :start_time AND end = :end AND end_time = :end_time COLLATE NOCASE)")
    suspend fun chapterExists(title: String, start: Double, start_time: String, end: Double, end_time: String): Boolean

    @Query("SELECT * FROM chapter WHERE chapter.audiobookId = :audiobookId ORDER BY chapter.start ASC")
    suspend fun getChaptersOfAudiobook(audiobookId: Long): List<Chapter>

    @Query("UPDATE chapter SET isPlaying = :isPlaying WHERE audiobookId = :audiobookId AND chapterId = :chapterId")
    suspend fun setChapterIsPlaying(audiobookId: Long, chapterId: Long, isPlaying: Boolean)

    @Query("UPDATE chapter SET isPlaying = 0 WHERE isPlaying = 1 AND audiobookId = :audiobookId")
    suspend fun resetChapterIsPlayingWhereNeeded(audiobookId: Long)

    @Transaction
    suspend fun resetBeforeSetChapterIsPlaying(audiobookId: Long, chapterId: Long, isPlaying: Boolean){
        resetChapterIsPlayingWhereNeeded(audiobookId)
        setChapterIsPlaying(audiobookId, chapterId, isPlaying)
    }

}