package de.erikspall.audiobookapp.data.data_source.local.database.dao

import androidx.room.*
import de.erikspall.audiobookapp.domain.model.Audiobook
import de.erikspall.audiobookapp.domain.model.AudiobookWithInfo
import de.erikspall.audiobookapp.domain.model.AudiobookWithPersons
import kotlinx.coroutines.flow.Flow

@Dao
interface AudiobookDao {

    @Query("SELECT * FROM audiobook")
    fun getAudiobooks(): Flow<List<Audiobook>>

    @Transaction
    @Query("SELECT * FROM audiobook")
    fun getAudiobooksWithPersons(): Flow<List<AudiobookWithPersons>>

    @Transaction
    @Query("SELECT * FROM audiobook")
    fun getAudiobooksWithInfo(): Flow<List<AudiobookWithInfo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(audiobook: Audiobook): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(audiobooks: List<Audiobook>)

    @Update
    suspend fun update(audiobook: Audiobook)

    @Delete
    suspend fun delete(audiobook: Audiobook)

    @Query("SELECT * FROM audiobook WHERE audiobookId = :id")
    suspend fun getAudiobookById(id: Int): Audiobook?

    @Query("SELECT * FROM audiobook WHERE uri = :uri")
    suspend fun getAudiobookByUri(uri: String): Audiobook?

    @Query("SELECT * FROM audiobook WHERE uri = :uri")
    suspend fun getAudiobookWithInfoByUri(uri: String): AudiobookWithInfo?

    @Query("SELECT EXISTS(SELECT * FROM audiobook WHERE uri = :uri COLLATE NOCASE)")
    suspend fun audiobookExists(uri: String): Boolean

    @Query("DELETE FROM audiobook")
    suspend fun deleteAll()

    @Query("UPDATE audiobook SET position = :position, isPlaying = :isPlaying WHERE audiobookId = :audiobookId")
    suspend fun setPosition(audiobookId: Long, position: Long, isPlaying: Boolean)
}