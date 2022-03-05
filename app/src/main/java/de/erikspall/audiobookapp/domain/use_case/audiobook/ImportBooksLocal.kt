package de.erikspall.audiobookapp.domain.use_case.audiobook

import de.erikspall.audiobookapp.domain.repository.AppRepository

class ImportBooksLocal(
    private val repository: AppRepository
) {
    suspend operator fun invoke() {
        repository.importAudiobooksFromLocalStorage()
    }
}