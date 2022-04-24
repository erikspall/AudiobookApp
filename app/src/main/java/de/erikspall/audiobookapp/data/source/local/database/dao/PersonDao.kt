package de.erikspall.audiobookapp.data.source.local.database.dao

import androidx.room.*
import de.erikspall.audiobookapp.domain.model.AuthorWithAudiobooks
import de.erikspall.audiobookapp.domain.model.Person
import de.erikspall.audiobookapp.domain.model.ReaderWithAudiobooks
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(person: Person): Long

    @Update
    suspend fun update(person: Person)

    @Delete
    suspend fun delete(person: Person)

    @Query("SELECT * FROM person WHERE personId = :id")
    fun getPerson(id: Int): Flow<List<Person>>

    @Query("SELECT * FROM person WHERE lower(firstName) = :firstName AND lower(lastName) = :lastName")
    suspend fun getPerson(firstName: String, lastName: String): Person?

    @Query("SELECT * FROM person")
    fun getPersons(): Flow<List<Person>>

    @Transaction
    @Query("SELECT * FROM person")
    fun getAuthorsWithAudiobooks(): Flow<List<AuthorWithAudiobooks>>

    @Transaction
    @Query("SELECT * FROM person")
    fun getReadersWithAudiobooks(): Flow<List<ReaderWithAudiobooks>>

    @Query("SELECT EXISTS(SELECT * FROM person WHERE lower(firstName) = :firstName AND lower(lastName) = :lastName COLLATE NOCASE)")
    suspend fun personExists(firstName: String, lastName: String): Boolean

    @Query("DELETE FROM person")
    suspend fun deleteAll()

}