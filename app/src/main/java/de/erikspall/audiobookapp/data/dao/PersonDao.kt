package de.erikspall.audiobookapp.data.dao

import androidx.room.*
import de.erikspall.audiobookapp.data.model.Person
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(person: Person)

    @Update
    suspend fun update(person: Person)

    @Delete
    suspend fun delete(person: Person)

    @Query("SELECT * FROM person WHERE personId = :id")
    fun getPerson(id: Int): Flow<List<Person>>

    @Query("SELECT * FROM person")
    fun getPersons(): Flow<List<Person>>

    @Query("DELETE FROM person")
    suspend fun reset()
}