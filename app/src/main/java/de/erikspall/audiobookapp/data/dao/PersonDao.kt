package de.erikspall.audiobookapp.data.dao

import androidx.room.*
import de.erikspall.audiobookapp.data.model.AuthorWithAudiobooks
import de.erikspall.audiobookapp.data.model.Person
import de.erikspall.audiobookapp.data.model.ReaderWithAudiobooks
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(person: Person): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSync(person: Person): Long

    @Update
    suspend fun update(person: Person)

    @Update
    fun updateSync(person: Person)

    @Delete
    suspend fun delete(person: Person)

    @Query("SELECT * FROM person WHERE personId = :id")
    fun getPerson(id: Int): Flow<List<Person>>

    @Query("SELECT * FROM person WHERE firstName = :firstName AND lastName = :lastName COLLATE NOCASE LIMIT 1")
    fun getPersonSync(firstName: String, lastName: String):Person

    @Query("SELECT * FROM person")
    fun getPersons(): Flow<List<Person>>

    @Transaction
    @Query("SELECT * FROM person")
    fun getAuthorsWithAudiobooks(): List<AuthorWithAudiobooks>

    @Transaction
    @Query("SELECT * FROM person")
    fun getReadersWithAudiobooks(): List<ReaderWithAudiobooks>

    @Query("SELECT EXISTS(SELECT * FROM person WHERE firstName = :firstName AND lastName = :lastName COLLATE NOCASE)")
    suspend fun personExists(firstName: String, lastName: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM person WHERE firstName = :firstName AND lastName = :lastName COLLATE NOCASE)")
    fun personExistsSync(firstName: String, lastName: String): Boolean

    @Query("DELETE FROM person")
    suspend fun deleteAll()

}