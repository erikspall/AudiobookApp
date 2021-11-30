package de.erikspall.audiobookapp.data.dao

import androidx.room.*
import de.erikspall.audiobookapp.data.model.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(genre: Genre)

    @Update
    suspend fun update(genre: Genre)

    @Delete
    suspend fun delete(genre: Genre)

    @Query("SELECT * FROM genre WHERE genreId = :id")
    fun getItem(id: Int): Flow<List<Genre>>

    @Query("SELECT * FROM genre")
    fun getItems(): Flow<List<Genre>>

    @Query("DELETE FROM genre")
    suspend fun reset()
}