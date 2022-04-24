package de.erikspall.audiobookapp.data.repository.database

import de.erikspall.audiobookapp.data.source.local.database.dao.PersonDao
import de.erikspall.audiobookapp.domain.model.AuthorWithAudiobooks
import de.erikspall.audiobookapp.domain.model.Person
import de.erikspall.audiobookapp.domain.model.ReaderWithAudiobooks
import de.erikspall.audiobookapp.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow

class PersonRepositoryImpl(
    private val personDao: PersonDao
) : PersonRepository {
    override suspend fun insert(person: Person): Long {
        return personDao.insert(person)
    }

    override suspend fun update(person: Person) {
        personDao.update(person)
    }

    override suspend fun delete(person: Person) {
        personDao.delete(person)
    }

    override fun getPerson(id: Int): Flow<List<Person>> {
        return personDao.getPerson(id)
    }

    override suspend fun getPerson(firstName: String, lastName: String): Person? {
        return personDao.getPerson(firstName.lowercase(), lastName.lowercase())
    }

    override fun getPersons(): Flow<List<Person>> {
        return personDao.getPersons()
    }

    override fun getAuthorsWithAudiobooks(): Flow<List<AuthorWithAudiobooks>> {
        return personDao.getAuthorsWithAudiobooks()
    }

    override fun getReadersWithAudiobooks(): Flow<List<ReaderWithAudiobooks>> {
        return personDao.getReadersWithAudiobooks()
    }

    override suspend fun personExists(firstName: String, lastName: String): Boolean {
        return personDao.personExists(firstName.lowercase(), lastName.lowercase())
    }

    override suspend fun deleteAll() {
        personDao.deleteAll()
    }
}