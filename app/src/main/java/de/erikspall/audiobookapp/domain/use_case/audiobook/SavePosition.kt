package de.erikspall.audiobookapp.domain.use_case.audiobook

import android.util.Log
import de.erikspall.audiobookapp.domain.repository.AppRepository

class SavePosition(
    private val repository: AppRepository
) {
    suspend operator fun invoke(bookUri: String, position: Long) {
        Log.d("Saving", "Saving position: $position for book: $bookUri")
        repository.setPosition(bookUri, position)
    }
}