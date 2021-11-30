package de.erikspall.audiobookapp.data.dao

import androidx.room.*
import de.erikspall.audiobookapp.data.model.AudiobookWithGenres
import de.erikspall.audiobookapp.data.model.GenreWithAudiobooks
import de.erikspall.audiobookapp.data.model.ReadBy
import de.erikspall.audiobookapp.data.model.WrittenBy
import kotlinx.coroutines.flow.Flow

@Dao
interface WrittenByDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(writtenBy: WrittenBy)

    @Update
    suspend fun update(writtenBy: WrittenBy)

    @Delete
    suspend fun delete(writtenBy: WrittenBy)


}