package de.erikspall.audiobookapp.domain.repository

import de.erikspall.audiobookapp.domain.model.AuthorWithAudiobooks
import de.erikspall.audiobookapp.domain.model.Person
import de.erikspall.audiobookapp.domain.model.ReaderWithAudiobooks
import kotlinx.coroutines.flow.Flow

interface PersonRepository {

    suspend fun insert(person: Person): Long

    suspend fun update(person: Person)

    suspend fun delete(person: Person)

    fun getPerson(id: Int): Flow<List<Person>>

    suspend fun getPerson(firstName: String, lastName: String): Person?

    fun getPersons(): Flow<List<Person>>

    fun getAuthorsWithAudiobooks(): Flow<List<AuthorWithAudiobooks>>

    fun getReadersWithAudiobooks(): Flow<List<ReaderWithAudiobooks>>

    suspend fun personExists(firstName: String, lastName: String): Boolean

    suspend fun deleteAll()
}
