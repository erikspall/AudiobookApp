package de.erikspall.audiobookapp.domain.use_case.audiobook

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import de.erikspall.audiobookapp.domain.model.Chapter
import de.erikspall.audiobookapp.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow

class GetChapters(
    private val repository: AppRepository
) {
    operator fun invoke(bookId: Long): LiveData<List<Chapter>> {
        Log.d("GetChapters", "Retrieving for book with id: $bookId")
        return repository.getChaptersOfAudiobook(bookId).asLiveData()
    }
}