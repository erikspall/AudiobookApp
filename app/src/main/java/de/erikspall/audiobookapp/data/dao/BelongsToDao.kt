package de.erikspall.audiobookapp.data.dao

import androidx.room.*
import de.erikspall.audiobookapp.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BelongsToDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(belongsTo: BelongsTo)

    @Update
    suspend fun update(belongsTo: BelongsTo)

    @Delete
    suspend fun delete(belongsTo: BelongsTo)

    @Transaction
    @Query("SELECT * FROM genre")
    fun getGenreWithAudiobooks(): Flow<List<GenreWithAudiobooks>>

    @Transaction
    @Query("SELECT * FROM audiobook")
    fun getAudiobookWithGenres(): Flow<List<AudiobookWithGenres>>

    @Transaction
    @Query("SELECT audiobook.* FROM audiobook JOIN belongsto ON audiobook.audiobookId = belongsto.audiobookId JOIN genre ON belongsto.genreId = genre.genreId WHERE genre.genreId = :genreId")
    fun getAllAudiobooksOfGenre(genreId: Int): Flow<List<Audiobook>>

}