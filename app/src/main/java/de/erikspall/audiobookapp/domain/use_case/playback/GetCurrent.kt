package de.erikspall.audiobookapp.domain.use_case.playback

import android.annotation.SuppressLint
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository

class GetCurrent(
    private val repository: PlayerControllerRepository
) {
    fun mediaItem(): MediaItem {
        return repository.getCurrentMediaItem()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun mediaItem(player: Player): MediaItem {
        return player.currentMediaItem ?: MediaItem.EMPTY
    }

    fun mediaMetaData(): MediaMetadata {
        return repository.getCurrentMediaMetadata()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun mediaMetaData(player: Player): MediaMetadata {
        return mediaItem(player).mediaMetadata
    }

    fun clippingConfiguration(): MediaItem.ClippingConfiguration {
        return repository.getCurrentClippingConfig()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun clippingConfiguration(player: Player): MediaItem.ClippingConfiguration {
        return mediaItem(player).clippingConfiguration
    }

    fun chapterDuration(): Long {
        return repository.getCurrentChapterDuration()
    }


    fun bookDuration(): Long {
        return repository.getCurrentBookDuration()
    }

    fun positionInBook(): Long {
        return repository.getCurrentPositionInBook()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun positionInBook(player: Player): Long {
        return clippingConfiguration(player).startPositionMs + player.contentPosition
    }

    fun positionInChapter(): Long {
        return repository.getCurrentPositionInChapter()
    }

    fun progressInBigPercent(): Int {
        return ((positionInChapter()
            .toDouble() / chapterDuration()) * 1000).toInt()
    }

    fun progressInBigPercent(chapterDuration: Long): Int {
        return ((positionInChapter()
            .toDouble() / chapterDuration) * 1000).toInt()
    }

    fun bookProgressInBigPercent(): Int {
        return ((positionInBook()
            .toDouble() / bookDuration()) * 1000).toInt()
    }

    fun bookProgressInBigPercent(bookDuration: Long): Int {
        return ((positionInBook()
            .toDouble() / bookDuration) * 1000).toInt()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun bookId(): Long {
        val raw = mediaItem().mediaId
        Log.d("PlaybackUseCases", "bookId() raw: $raw")
        if (raw.isEmpty())
            return -1

        val firstIndex = raw.indexOf("]") + 1
        Log.d("PlaybackUseCases", "bookId() firstIndex: $firstIndex")
        val lastIndex = raw.indexOf("[", startIndex = firstIndex + 1)
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