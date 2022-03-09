package de.erikspall.audiobookapp.domain.use_case.playback

import android.util.Log
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.METADATA_BOOK_ID
import de.erikspall.audiobookapp.domain.const.Player
import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository
import java.lang.IllegalStateException

class TogglePlayback(
    private val controllerRepo: PlayerControllerRepository
) {
    operator fun invoke() {
        if (controllerRepo.getController()?.playbackState != Player.STATE_IDLE) {
            Log.d(
                "PlaybackUseCases",
                "Player state: ${controllerRepo.getController()?.playbackState}"
            )
            if (controllerRepo.isPlaying()) {
                Log.d("PlaybackUseCases", "pause!")
                controllerRepo.pause()
            } else {
                Log.d("PlaybackUseCases", "play!")
                controllerRepo.play()
            }
        } else if (controllerRepo.getController() != null) {
            // Prepare player again
            val bookId =
                controllerRepo.getCurrentMediaMetadata().extras?.getLong(
                    METADATA_BOOK_ID
                ) ?: -1
            val position = controllerRepo.getCurrentPositionInBook()

            controllerRepo.initController {
                val chapters = controllerRepo.getChapterMediaItemsOf(bookId)

                if (chapters.isEmpty())
                    Log.e(
                        "PlaybackUseCases",
                        "Chapters not found for $bookId"
                    )
                else {
                    Log.d("PlaybackUseCases", "Setting ${chapters.size} chapters")
                    controllerRepo.play(chapters, false, position)
                }
            }
        } else {
            throw IllegalStateException("Tried to toggle play/pause but player is in idle and controller is null!")
        }
    }
}