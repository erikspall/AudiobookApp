package de.erikspall.audiobookapp.data.source.local.database.dao

import androidx.room.*
import de.erikspall.audiobookapp.domain.model.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(genre: Genre): Long

    @Update
    suspend fun update(genre: Genre)

    @Query("SELECT EXISTS(SELECT * FROM genre WHERE name = :genre COLLATE NOCASE)")
    suspend fun genreExists(genre: String): Boolean

    @Delete
    suspend fun delete(genre: Genre)

    @Query("SELECT * FROM genre WHERE name = :name COLLATE NOCASE LIMIT 1")
    suspend fun getGenre(name: String): Genre?

    @Query("DELETE FROM genre")
    suspend fun deleteAll()

    @Query("SELECT genre.* FROM genre JOIN belongsto ON genre.genreId = belongsto.genreId JOIN audiobook ON audiobook.audiobookId = belongsto.audiobookId WHERE audiobook.audiobookId = :audiobookId")
    fun getGenresOf(audiobookId: Long): Flow<List<Genre>>

}