package de.erikspall.audiobookapp.data.viewmodels

import android.util.Log
import androidx.lifecycle.*
import de.erikspall.audiobookapp.data.model.*
import de.erikspall.audiobookapp.data.repository.DatabaseRepository
import kotlinx.coroutines.launch

class DatabaseViewModel(
    private val repository: DatabaseRepository
    ): ViewModel() {

    // Using LiveData and caching what all* returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allAudiobooks: LiveData<List<Audiobook>> = repository.allAudiobooks.asLiveData()
    val allAudiobooksWithAuthor: LiveData<List<AudiobookWithAuthor>> = repository.allAudiobooksWithAuthor.asLiveData()
    val allAudiobooksWithInfo: LiveData<List<AudiobookWithInfo>> = repository.allAudiobooksWithInfo.asLiveData()
    //TODO: Do I really need them?:
    val allGenres: LiveData<List<Genre>> = repository.allGenres.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insertAudiobook(audiobook: Audiobook) = viewModelScope.launch {
        repository.insertAudiobook(audiobook)
    }

    fun insertPerson(person: Person) = viewModelScope.launch {
        repository.insertPerson(person)
    }


    fun insertAudiobooks(audiobooks: List<Audiobook>) = viewModelScope.launch {
        repository.insertAudiobooks(audiobooks)
        Log.d("Hey", "Im doin someting")
    }

    fun getAudiobookWithInfo(uri: String): AudiobookWithInfo{
        return repository.getAudiobookWithInfo(uri)
    }

}

class DatabaseViewModelFactory(
    private val repository: DatabaseRepository
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DatabaseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}