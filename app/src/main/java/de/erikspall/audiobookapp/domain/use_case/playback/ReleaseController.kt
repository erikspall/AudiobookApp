package de.erikspall.audiobookapp.domain.use_case.playback

import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository

class ReleaseController(
    private val controllerRepo: PlayerControllerRepository
) {
    operator fun invoke() {
        controllerRepo.releaseController()
    }
}