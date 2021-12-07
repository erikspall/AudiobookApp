package de.erikspall.audiobookapp.data.dao

import androidx.room.*
import de.erikspall.audiobookapp.data.model.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(genre: Genre): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSync(genre: Genre): Long

    @Update
    suspend fun update(genre: Genre)

    @Delete
    suspend fun delete(genre: Genre)

    @Query("SELECT * FROM genre WHERE genreId = :id")
    fun getItem(id: Int): Flow<List<Genre>>

    @Query("SELECT * FROM genre")
    fun getItems(): Flow<List<Genre>>

    @Query("SELECT EXISTS(SELECT * FROM genre WHERE name = :genre COLLATE NOCASE)")
    fun genreExistsSync(genre: String): Boolean

    @Query("SELECT * FROM genre WHERE name = :genre COLLATE NOCASE LIMIT 1")
    fun getGenreSync(genre: String): Genre

    @Query("DELETE FROM genre")
    suspend fun deleteAll()
}
