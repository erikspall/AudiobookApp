package de.erikspall.audiobookapp.domain.use_case.playback

import android.annotation.SuppressLint
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.BOOK_PREFIX
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.METADATA_BOOK_ID
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.METADATA_CHAPTER_ID
import de.erikspall.audiobookapp.domain.model.Chapter
import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository
import kotlinx.coroutines.flow.Flow

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

    fun bookId(): Long {
        return repository.getCurrentMediaMetadata().extras?.getLong(METADATA_BOOK_ID) ?: -1
    }

    fun chapterId(): Long {
        return repository.getCurrentMediaMetadata().extras?.getLong(METADATA_CHAPTER_ID) ?: -1
    }

    fun bookId(player: Player): Long {
        return player.currentMediaItem?.mediaMetadata?.extras?.getLong(METADATA_BOOK_ID) ?: -1
    }

    fun chapterId(player: Player): Long {
        return player.currentMediaItem?.mediaMetadata?.extras?.getLong(METADATA_CHAPTER_ID) ?: -1
    }
}