package de.erikspall.audiobookapp.domain.use_case.playback

import de.erikspall.audiobookapp.domain.model.AudiobookWithInfo
import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository
import de.erikspall.audiobookapp.domain.use_case.audiobook.GetBooksWithInfo

class Initizialize(
    private val controllerRepository: PlayerControllerRepository
) {
    fun controller(onControllerCreated: () -> Unit){
        controllerRepository.initController {
            onControllerCreated()
        }
    }

    fun mediaItemTree(books: List<AudiobookWithInfo>) {
        controllerRepository.updateMediaTree(books)
    }

    fun mediaItemTree(getBooksWithInfo: GetBooksWithInfo) {
        controllerRepository.updateMediaTree(getBooksWithInfo)
    }
}