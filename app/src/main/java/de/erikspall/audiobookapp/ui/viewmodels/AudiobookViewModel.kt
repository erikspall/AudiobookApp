package de.erikspall.audiobookapp.ui.viewmodels

import androidx.core.net.UriCompat
import androidx.lifecycle.*
import de.erikspall.audiobookapp.data.dao.AudiobookDao
import de.erikspall.audiobookapp.data.dao.BelongsToDao
import de.erikspall.audiobookapp.data.dao.GenreDao
import de.erikspall.audiobookapp.data.dao.PersonDao
import de.erikspall.audiobookapp.data.model.Audiobook
import de.erikspall.audiobookapp.data.model.BelongsTo
import de.erikspall.audiobookapp.data.model.Genre
import de.erikspall.audiobookapp.data.model.Person
import kotlinx.coroutines.launch

class DatabaseViewModel(
    private val audiobookDao: AudiobookDao,
    private val personDao: PersonDao,
    private val genreDao: GenreDao,
    private val belongsToDao: BelongsToDao
    ): ViewModel() {
    fun allAudiobooks(): LiveData<List<Audiobook>> = audiobookDao.getAudiobooks().asLiveData()
    fun allPersons(): LiveData<List<Person>> = personDao.getPersons().asLiveData()
    fun allGenres(): LiveData<List<Genre>> = genreDao.getItems().asLiveData()

    // ------------------------ Audiobook -------------------------
    private fun insertAudiobook(audiobook: Audiobook) {
        // Coroutine off main thread
        viewModelScope.launch {
            audiobookDao.insert(audiobook)
        }
    }

    private fun getNewAudiobookEntry(audiobookPath: String, audiobookTitle: String, audiobookProgress: Int): Audiobook {
        return Audiobook(
            audiobookPath = audiobookPath,
            audiobookTitle = audiobookTitle,
            audiobookProgress = audiobookProgress
        )
    }

    fun addNewAudiobook(audiobookPath: String, audiobookTitle: String, audiobookProgress: Int) {
        val newAudiobook = getNewAudiobookEntry(audiobookPath, audiobookTitle, audiobookProgress)
        insertAudiobook(newAudiobook)
    }

    fun isEntryValid(audiobookPath: UriCompat, audiobookTitle: String, audiobookProgress: Int): Boolean {
        if (audiobookProgress in 0..100)
            return true
        return false
    }

    // ------------------------ Audiobook -------------------------
    private fun insertPerson(person: Person) {
        // Coroutine off main thread
        viewModelScope.launch {
            personDao.insert(person)
        }
    }

    private fun getNewPersonEntry(firstName: String, lastName: String): Person {
        return Person(
            firstName = firstName,
            lastName = lastName
        )
    }

    fun addNewPerson(firstName: String, lastName: String) {
        val newPerson = getNewPersonEntry(firstName, lastName)
        insertPerson(newPerson)
    }

    fun isEntryValid(firstName: String, lastName: String): Boolean {
        if (firstName.isNotBlank() xor lastName.isNotBlank())
            return true
        return false
    }

    // ------------------------ Genre -------------------------
    private fun insertGenre(genre: Genre) {
        // Coroutine off main thread
        viewModelScope.launch {
            genreDao.insert(genre)
        }
    }

    private fun getNewGenreEntry(genreName: String): Genre {
        return Genre(
            genreName = genreName
        )
    }

    fun addNewGenre(genreName: String) {
        val newGenre = getNewGenreEntry(genreName)
        insertGenre(newGenre)
    }

    fun isEntryValid(genreName: String): Boolean {
        if (genreName.isNotBlank())
            return true
        return false
    }

    // ------------------------ BelongsTo -------------------------
    private fun insertBelongsTo(belongsTo: BelongsTo) {
        // Coroutine off main thread
        viewModelScope.launch {
            belongsToDao.insert(belongsTo)
        }
    }

    private fun getNewBelongsToEntry(audiobookId: Int, genreId: Int): BelongsTo {
        return BelongsTo(
            audiobookId = audiobookId,
            genreId = genreId
        )
    }

    fun addNewBelongsTo(audiobookId: Int, genreId: Int) {
        val newBelongTo = getNewBelongsToEntry(audiobookId, genreId)
        insertBelongsTo(newBelongTo)
    }

    fun isEntryValid(audiobookId: Int, genreId: Int): Boolean {
        //TODO: Not implemented yet
        return true
    }

    fun getAllAudiobooksOfGenre(genreId: Int): LiveData<List<Audiobook>> {
       return belongsToDao.getAllAudiobooksOfGenre(genreId).asLiveData()
    }

}

class DatabaseViewModelFactory(
    private val audiobookDao: AudiobookDao,
    private val personDao: PersonDao,
    private val genreDao: GenreDao,
    private val belongsToDao: BelongsToDao
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DatabaseViewModel(audiobookDao, personDao, genreDao, belongsToDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}