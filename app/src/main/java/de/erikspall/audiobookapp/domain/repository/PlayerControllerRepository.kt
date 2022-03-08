package de.erikspall.audiobookapp.domain.repository

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import de.erikspall.audiobookapp.data.data_source.local.player_controller.MediaItemTree
import de.erikspall.audiobookapp.domain.model.AudiobookWithInfo
import de.erikspall.audiobookapp.domain.use_case.audiobook.GetBooksWithInfo
import kotlinx.coroutines.flow.Flow

interface PlayerControllerRepository {
    fun getMediaTree(): MediaItemTree
    fun updateMediaTree(books: List<AudiobookWithInfo>)
    fun updateMediaTree(getBooksWithInfo: GetBooksWithInfo)
    fun getController(): MediaController?
    fun getCurrentMediaMetadata(): MediaMetadata
    fun getCurrentMediaItem(): MediaItem
    fun getCurrentClippingConfig(): MediaItem.ClippingConfiguration
    fun getCurrentPositionInBook(): Long
    fun getCurrentPositionInChapter(): Long
    fun getEndPositionOfItemAt(index: Int): Long
    fun getStartPositionOfItemAt(index: Int): Long
    fun getCurrentChapterDuration(): Long
    fun releaseController()
    fun initController(onControllerCreated: () -> Unit)
    fun isPlaying(): Boolean
    fun play()
    fun pause()
    fun getChapterMediaItemsOf(bookId: Long): List<MediaItem>
    fun play(chapters: List<MediaItem>, fromStart: Boolean, lastPosition: Long)
    fun currentMediaItemCount(): Int
    fun seekTo(chapterIndex: Int, position: Long)
    fun seekTo(position: Long)
    fun getCurrentBookDuration(): Long
    fun getDurationOfBook(uri: Uri): Long
    fun getDurationOfChapter(mediaId: String): Long
}