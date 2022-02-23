package de.erikspall.audiobookapp.data.source.local.database.dao

import androidx.room.*
import de.erikspall.audiobookapp.data.source.local.database.model.Audiobook
import de.erikspall.audiobookapp.data.source.local.database.model.AudiobookWithAuthor
import de.erikspall.audiobookapp.data.source.local.database.model.AudiobookWithChapters
import de.erikspall.audiobookapp.data.source.local.database.model.AudiobookWithInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AudiobookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(audiobook: Audiobook): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSync(audiobook: Audiobook): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(audiobooks: List<Audiobook>)

    @Update
    suspend fun update(audiobook: Audiobook)

    @Delete
    suspend fun delete(audiobook: Audiobook)

    @Query("SELECT * FROM audiobook WHERE audiobookId = :id")
    fun getAudiobook(id: Int): Flow<List<Audiobook>>

    @Query("SELECT * FROM audiobook WHERE title = :title AND uri = :uri COLLATE NOCASE LIMIT 1")
    fun getAudiobookSync(title: String, uri: String): Audiobook

    @Query("SELECT * FROM audiobook")
    fun getAudiobooks(): Flow<List<Audiobook>>

    @Query("SELECT EXISTS(SELECT * FROM audiobook WHERE title = :title AND uri = :uri COLLATE NOCASE)")
    fun audiobookExistsSync(title: String, uri: String): Boolean//TODO: Uri could change?

    @Query("DELETE FROM audiobook")
    suspend fun deleteAll()

    @Query("SELECT * FROM audiobook")
    fun getAudiobooksWithChapters(): Flow<List<AudiobookWithChapters>> // Flow probably overkill

    @Transaction
    @Query("SELECT * FROM audiobook")
    fun getAudiobooksWithAuthor(): Flow<List<AudiobookWithAuthor>>

    @Transaction
    @Query("SELECT * FROM audiobook")
    fun getAudiobooksWithAuthorSync(): List<AudiobookWithAuthor>

    @Transaction
    @Query("SELECT * FROM audiobook")
    fun getAudiobooksWithInfo(): Flow<List<AudiobookWithInfo>>

    @Transaction
    @Query("SELECT * FROM audiobook WHERE uri = :uri LIMIT 1")
    fun getAudiobookWithInfo(uri: String): AudiobookWithInfo

    @Transaction
    @Query("SELECT * FROM audiobook")
    fun getAudiobooksWithInfoSync(): List<AudiobookWithInfo>

    @Transaction
    @Query("SELECT coverUri FROM audiobook WHERE coverUri LIKE '%' || :mediaId LIMIT 1")
    fun getCoverUriSync(mediaId: Int): String

    @Query("UPDATE audiobook SET position = :position WHERE uri = :uri")
    suspend fun setPosition(uri: String, position: Long)
}