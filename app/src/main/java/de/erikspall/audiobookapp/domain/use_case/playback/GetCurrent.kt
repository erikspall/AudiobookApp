package de.erikspall.audiobookapp.domain.use_case.playback

import android.annotation.SuppressLint
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository

class GetCurrent(
    private val repository: PlayerControllerRepository
) {
    fun mediaItem(): MediaItem {
        return repository.getCurrentMediaItem()
    }

    fun mediaMetaData(): MediaMetadata {
        return repository.getCurrentMediaMetadata()
    }

    fun clippingConfiguration(): MediaItem.ClippingConfiguration{
        return repository.getCurrentClippingConfig()
    }

    fun chapterDuration(): Long {
        return repository.getCurrentChapterDuration()
    }

    fun positionInBook(): Long {
        return repository.getCurrentPositionInBook()
    }
    fun positionInChapter(): Long {
        return repository.getCurrentPositionInChapter()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun bookId(): Long {
        val raw = mediaItem().mediaId
        Log.d("PlaybackUseCases", "bookId() raw: $raw")
        if (raw.isEmpty())
            return -1

        val firstIndex = raw.indexOf("]") + 1
        Log.d("PlaybackUseCases", "bookId() firstIndex: $firstIndex")
        val lastIndex = raw.indexOf("[", startIndex = firstIndex+1)
        Log.d("PlaybackUseCases", "bookId() lastIndex: $lastIndex")
        val idString = when (lastIndex) {
            -1 -> raw.substring(firstIndex)
            else -> raw.substring(firstIndex, lastIndex)
        }
        Log.d("PlaybackUseCases", "bookId() idString: $idString")
        return if (idString.isEmpty() || !idString.isNumeric())
            -1
        else
            idString.toLong()
    }

    private fun String.isNumeric() = this.all { it in '0'..'9' }
}