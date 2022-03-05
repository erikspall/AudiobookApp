package de.erikspall.audiobookapp.domain.use_case.playback

import android.util.Log
import de.erikspall.audiobookapp.domain.model.Audiobook
import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository

class PlayBook(
    private val controllerRepo: PlayerControllerRepository
) {
    operator fun invoke(book: Audiobook, fromStart: Boolean = false){
        val chapters = controllerRepo.getChapterMediaItemsOf(book.audiobookId)
        if (chapters.isEmpty())
            Log.e(
                "PlaybackUseCases",
                "Chapters not found for $book"
            )
        else {
            controllerRepo.play(chapters, fromStart, book.position)
        }
    }
}