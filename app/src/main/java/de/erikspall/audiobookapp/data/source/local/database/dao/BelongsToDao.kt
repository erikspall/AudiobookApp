package de.erikspall.audiobookapp.data.source.local.database.dao

import androidx.room.*
import de.erikspall.audiobookapp.domain.model.Audiobook
import de.erikspall.audiobookapp.domain.model.AudiobookWithGenres
import de.erikspall.audiobookapp.domain.model.BelongsTo
import de.erikspall.audiobookapp.domain.model.GenreWithAudiobooks
import kotlinx.coroutines.flow.Flow

@Dao
interface BelongsToDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(belongsTo: BelongsTo): Long

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
    fun getAllAudiobooksOfGenre(genreId: Long): Flow<List<Audiobook>>

    @Query("INSERT INTO belongsto VALUES (:genreId, :audiobookId)")
    suspend fun addGenreToAudiobook(genreId: Long?, audiobookId: Long?)

    @Query("DELETE FROM belongsto")
    suspend fun deleteAll()
}