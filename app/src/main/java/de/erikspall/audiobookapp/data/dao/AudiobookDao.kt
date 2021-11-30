package de.erikspall.audiobookapp.data.dao

import androidx.room.*
import de.erikspall.audiobookapp.data.model.Audiobook
import kotlinx.coroutines.flow.Flow

@Dao
interface AudiobookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(audiobook: Audiobook)

    @Update
    suspend fun update(audiobook: Audiobook)

    @Delete
    suspend fun delete(audiobook: Audiobook)

    @Query("SELECT * FROM audiobook WHERE audiobookId = :id")
    fun getAudiobook(id: Int): Flow<List<Audiobook>>

    @Query("SELECT * FROM audiobook")
    fun getAudiobooks(): Flow<List<Audiobook>>

    @Query("DELETE FROM audiobook")
    suspend fun reset()


}